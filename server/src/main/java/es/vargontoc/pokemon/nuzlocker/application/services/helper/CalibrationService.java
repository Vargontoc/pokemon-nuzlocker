package es.vargontoc.pokemon.nuzlocker.application.services.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec.GenerationCodec;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.RomId;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.GameProfile;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.SymbolExpression;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.EmulationGameData;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.Gen3Codec;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.ProfileRegistry;
import es.vargontoc.pokemon.nuzlocker.application.services.SnapshotService;

@Service
public class CalibrationService {


     static final long EWRAM_START = 0x02000000L;
    static final long EWRAM_END = 0x02040000L;
    static final long IWRAM_START = 0x03000000L;
    static final long IWRAM_END = 0x03008000L;

    private static final RomId REFERENCE = new RomId("BPRE", 0);
    private static final String REFERENCE_SAVEBLOCK_PTR = "0x03005008";
    private static final int LOCATION_OFFSET = 0x4;
    private static final int MAX_PARTY = 6;
    private static final int MAX_KANTO_JOHTO = 251;

    private final EmulatorTransportPort transport;
    private final GenerationCodec codec;
    private final ProfileRegistry profiles;
    private final EmulationGameData data;
    private final SnapshotService snapshots;
    private Set<Long> saveBlockCandidates;
    private byte[] flagsBaseline;

    public CalibrationService(EmulatorTransportPort transport, Gen3Codec codec, ProfileRegistry profiles, EmulationGameData data,
            SnapshotService snapshots) {
        this.transport = transport;
        this.codec = codec;
        this.profiles = profiles;
        this.data = data;
        this.snapshots = snapshots;
    }
 
    // ------------------------------------------------------------------ equipo
 
    public record PartyCandidate(String address, int count, List<Integer> species, List<Integer> levels) {}
 
    public List<PartyCandidate> scanParty() throws IOException {
        return findPartyRuns(MemoryDump.read(transport, EWRAM_START, EWRAM_END), codec);
    }
 
    /** Secuencias de Pokémon válidos consecutivos; la más larga suele ser tu equipo. */
    static List<PartyCandidate> findPartyRuns(MemoryDump memory, GenerationCodec codec) {
        int size = codec.monSize();
        List<PartyCandidate> candidates = new ArrayList<>();
        Set<Long> covered = new HashSet<>();
        for (long address = memory.start(); address + size <= memory.end(); address += 4) {
            if (covered.contains(address)) {
                continue;
            }
            List<PartyMon> run = new ArrayList<>();
            long current = address;
            while (run.size() < MAX_PARTY && memory.contains(current, size)) {
                PartyMon mon = codec.decode(memory.slice(current, size));
                if (!isPlausible(mon)) {
                    break;
                }
                run.add(mon);
                covered.add(current);
                current += size;
            }
            if (!run.isEmpty()) {
                candidates.add(new PartyCandidate(hex(address), run.size(),
                        run.stream().map(PartyMon::species).toList(), run.stream().map(PartyMon::level).toList()));
            }
        }
        candidates.sort(Comparator.comparingInt(PartyCandidate::count).reversed());
        return candidates;
    }
 
    private static boolean isPlausible(PartyMon mon) {
        return mon != null && mon.valid()
                && mon.species() >= 1 && mon.species() <= MAX_KANTO_JOHTO
                && mon.level() >= 1 && mon.level() <= 100
                && mon.maxHp() > 0 && mon.hp() <= mon.maxHp();
    }
 
    // ------------------------------------------------------------------ save block
 
    public record SaveBlockCalibration(String location, List<String> candidates, String message) {}
 
    /**
     * Llama a esto en varias localizaciones distintas (Pueblo Paleta, Ruta 1, Ciudad Verde...).
     * Cada llamada descarta los punteros que no apuntan al mapa en el que estás.
     */
    public synchronized SaveBlockCalibration narrowSaveBlock(String locationId) throws IOException {
        int[] map = data.mapFor(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Localización sin mapa en map-locations.json: " + locationId));
        Set<Long> matches = matchSaveBlockPointers(MemoryDump.read(transport, IWRAM_START, IWRAM_END),
                MemoryDump.read(transport, EWRAM_START, EWRAM_END), map[0], map[1]);
        if (saveBlockCandidates == null) {
            saveBlockCandidates = new TreeSet<>(matches);
        } else {
            saveBlockCandidates.retainAll(matches);
        }
 
        List<String> list = saveBlockCandidates.stream().map(CalibrationService::hex).toList();
        String message = switch (list.size()) {
            case 0 -> "Ningún candidato: reinicia la calibración (DELETE) y comprueba que la localización indicada es donde estás";
            case 1 -> "Puntero al save block encontrado: " + list.getFirst();
            default -> "Quedan %d candidatos: ve a otra localización y vuelve a llamar".formatted(list.size());
        };
        return new SaveBlockCalibration(locationId, list, message);
    }
 
    public synchronized void resetSaveBlock() {
        saveBlockCandidates = null;
    }

    // ------------------------------------------------------------------ flags

    public record FlagDiff(int flagId, String hex, boolean before, boolean after) {}

    /**
     * Fija el estado actual de las flags como punto de partida. Llama a esto justo ANTES
     * de hacer la acción que quieres calibrar (conseguir una medalla, hablar con Oak...).
     */
    public synchronized void markFlags() throws IOException {
        flagsBaseline = snapshots.read().flags().clone();
    }

    public synchronized void resetFlags() {
        flagsBaseline = null;
    }

    /**
     * Compara las flags actuales contra el punto de partida marcado con {@link #markFlags()}.
     * Los flagId que aparecen son candidatos: si solo cambia uno, es el que buscas; si salen
     * varios, repite la prueba en otro momento sin hacer esa acción y descarta los que se repitan
     * (contadores internos del motor que cambian solos).
     */
    public synchronized List<FlagDiff> diffFlags() throws IOException {
        if (flagsBaseline == null) {
            throw new IllegalStateException("Llama antes a POST /emulator/calibration/flags para fijar el punto de partida");
        }
        byte[] now = snapshots.read().flags();
        List<FlagDiff> changes = new ArrayList<>();
        int bits = Math.min(flagsBaseline.length, now.length) * 8;
        for (int flagId = 0; flagId < bits; flagId++) {
            boolean before = bit(flagsBaseline, flagId);
            boolean after = bit(now, flagId);
            if (before != after) {
                changes.add(new FlagDiff(flagId, "0x%03X".formatted(flagId), before, after));
            }
        }
        return changes;
    }

    private static boolean bit(byte[] flags, int flagId) {
        int index = flagId / 8;
        return index < flags.length && ((flags[index] >> (flagId & 7)) & 1) == 1;
    }
 
    /** Direcciones de IWRAM que contienen un puntero a EWRAM cuyo destino + 4 guarda el mapa actual. */
    static Set<Long> matchSaveBlockPointers(MemoryDump iwram, MemoryDump ewram, int mapGroup, int mapNum) {
        Set<Long> matches = new TreeSet<>();
        for (long address = iwram.start(); address + 4 <= iwram.end(); address += 4) {
            long pointer = iwram.u32(address);
            if (ewram.contains(pointer + LOCATION_OFFSET, 2)
                    && ewram.u8(pointer + LOCATION_OFFSET) == mapGroup
                    && ewram.u8(pointer + LOCATION_OFFSET + 1) == mapNum) {
                matches.add(address);
            }
        }
        return matches;
    }
 
    // ------------------------------------------------------------------ perfil sugerido
 
    public record ProfileSuggestion(String fileName, Map<String, Object> profile, List<String> checks) {}
 
    public synchronized ProfileSuggestion suggestProfile() throws IOException {
        RomId rom = transport.info();
        GameProfile reference = profiles.require(REFERENCE);
        List<PartyCandidate> parties = scanParty();
        if (parties.isEmpty()) {
            throw new IllegalStateException("No se ha encontrado ningún equipo: carga una partida con al menos un Pokémon");
        }
 
        List<String> checks = new ArrayList<>();
        PartyCandidate party = parties.getFirst();
        long delta = Long.decode(party.address()) - fixed(reference, "party");
        checks.add("party: %s, %d Pokémon (especies %s, niveles %s). Comprueba que es tu equipo"
                .formatted(party.address(), party.count(), party.species(), party.levels()));
 
        String saveBlockPtr = REFERENCE_SAVEBLOCK_PTR;
        if (saveBlockCandidates != null && saveBlockCandidates.size() == 1) {
            saveBlockPtr = hex(saveBlockCandidates.iterator().next());
            checks.add("Puntero al save block calibrado: " + saveBlockPtr);
        } else {
            checks.add("Puntero al save block SIN calibrar: se usa el de la versión USA (%s). Calíbralo con /emulator/calibrate/saveblock"
                    .formatted(REFERENCE_SAVEBLOCK_PTR));
        }
 
        Map<String, String> symbols = new LinkedHashMap<>();
        for (Map.Entry<String, SymbolExpression> e : reference.symbols().entrySet()) {
            SymbolExpression expression = e.getValue();
            if (expression.isFixed() && fixed(reference, e.getKey()) >= EWRAM_START && fixed(reference, e.getKey()) < EWRAM_END) {
                symbols.put(e.getKey(), hex(fixed(reference, e.getKey()) + delta));
            } else {
                symbols.put(e.getKey(), expression.toString().replace(REFERENCE_SAVEBLOCK_PTR, saveBlockPtr));
            }
        }
 
        if (symbols.containsKey("partyCount")) {
            int count = transport.read(Long.decode(symbols.get("partyCount")), 1)[0] & 0xFF;
            checks.add(count == party.count()
                    ? "partyCount (%s) = %d: coincide con el equipo".formatted(symbols.get("partyCount"), count)
                    : "partyCount (%s) = %d, pero el equipo tiene %d: dirección estimada incorrecta".formatted(symbols.get("partyCount"), count, party.count()));
        }
        checks.add("enemyParty y battleTypeFlags son estimaciones: entra en un combate y comprueba enemyLead y trainerBattle en /emulator/snapshot");
        checks.add("flags: consigue la primera medalla o el inicial y comprueba badges/flags en /emulator/snapshot");
 
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("romCode", rom.gameCode());
        profile.put("revision", rom.revision());
        profile.put("description", "Perfil calibrado a partir de %s, pendiente de verificar".formatted(reference.key()));
        profile.put("pointerSize", reference.pointerSize());
        profile.put("pointerRange", List.of(hex(reference.pointerMin()), hex(reference.pointerMax())));
        profile.put("symbols", symbols);
        Map<String, String> sizes = new LinkedHashMap<>();
        reference.sizes().forEach((k, v) -> sizes.put(k, "0x%X".formatted(v)));
        profile.put("sizes", sizes);
        return new ProfileSuggestion(rom.key() + ".json", profile, checks);
    }
 
    private static long fixed(GameProfile profile, String symbol) throws IOException {
        return profile.address(symbol, address -> {
            throw new IllegalStateException("El símbolo " + symbol + " no es una dirección fija");
        });
    }
 
    static String hex(long value) {
        return "0x%08X".formatted(value);
    }
}
