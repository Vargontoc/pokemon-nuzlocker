package es.vargontoc.pokemon.nuzlocker.domain.services;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;

import static es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision.*;

import java.util.List;
import java.util.OptionalInt;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.SpecieFamilyUseCase;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Location;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.LevelCapModeType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RuleReasonType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunStatusType;

public class NuzlockeRulesEngine {
    
    private final SpecieFamilyUseCase families;

    public NuzlockeRulesEngine(SpecieFamilyUseCase families) {
        this.families = families;
    }


    public RuleDecision evaluateEncounter(RunNuzlocke run, Location location, String specie, boolean shiny, List<EncounterRecord> history, List<RunPokemon> owned) {
        NuzlockeRules rules = run.rules();

        if(run.status() != RunStatusType.ACTIVE)
            return deny(RuleReasonType.RUN_NOT_ACTIVE, "La partida ya ha terminado");

        if(!run.isCatchingUnlocked())
            return deny(RuleReasonType.CATCHING_NOT_UNLOCKED, "Aún no tienes Poké balls: este encuentro no cuenta para la zona");

        if(location.encounters().isEmpty())
            return deny(RuleReasonType.NO_ENCOUNTERS_IN_LOCATION, "En %s no hay Pokémon salvajes".formatted(location.name()));

        if(shiny && rules.shinyClause())
            return allow(RuleReasonType.SHINY_CLAUSE, false, "Shiny: se puede capturar sin gastar el encuentro en la zona");

        boolean zoneUsed = history.stream().anyMatch(e -> e.consumeZone() && e.locationId().equals(location.id()));
        if(zoneUsed)
            return deny(RuleReasonType.ZONE_ALREADY_USED, "Ya se usó el encuentro en %s".formatted(location.name()));

        if(rules.dupesClause()){
            String family = families.familyOf(specie);
            boolean duplicate = owned.stream().anyMatch(p -> families.familyOf(p.specie()).equals(family));
            if(duplicate)
                return deny(RuleReasonType.DUPLICATE_SPECIE, "Ya tienes (o tuviste) un %s: el encuentro no cuenta y puedes seguir buscando en %s".formatted(specie, location.name()));
        }

        return allow(RuleReasonType.FIRST_ENCOUNTER, true, "Primer encuentro de %s: si no lo capturas, la zona queda gastada".formatted(location.name()));
    }

    public RuleDecision evaluateLevel(NuzlockeRules rules, int level, OptionalInt levelCap) {
        if(rules.levelCap() == LevelCapModeType.NONE || levelCap.isEmpty())
            return allow(RuleReasonType.NO_LEVEL_CAP, false, "Sin límite de nivel");

        int cap = levelCap.getAsInt();
        if(level > cap)
            return deny(RuleReasonType.OVER_LEVEL_CAP, "Nivel %d supera el límite de %d".formatted(level, cap));
        return allow(RuleReasonType.WITHIN_LEVEL_CAP, false, "Nivel %d dentro del límite de %d".formatted(level, cap));
    }

    public RuleDecision evaluateBattleItem(NuzlockeRules rules) {
        return rules.battleItemsAllowed() ? 
            allow(RuleReasonType.BATTLE_ITEMS_ALLOWED, false, "Se permiten objetos en combate") :
            deny(RuleReasonType.BATTLE_ITEMS_FORBIDDEN, "No se permiten objetos en combate");
    }
}
