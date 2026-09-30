package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

import java.util.List;

/** Cambios en la lectura de la ram */
public sealed interface BridgeSignal {
    
    /**  */
    record FlagSet(String flag) implements BridgeSignal {}

    record BadgeObtained(String badge) implements BridgeSignal {}

    record LocationReached(String locationId) implements BridgeSignal {}

    record WildBattleStarted(PartyMon enemy) implements BridgeSignal {}

    record TrainerBattleStarted(List<PartyMon> team) implements BridgeSignal {}

    record TrainerBattleWon(List<PartyMon> team) implements BridgeSignal {}
}
