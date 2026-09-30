package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.BridgeSignal;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.EmulationGameData;

@Component
public class SnapshotDiffer {
    
    final EmulationGameData data;

    public SnapshotDiffer(EmulationGameData data) {
        this.data = data;
    }

    public List<BridgeSignal> diff(GameSnapshot previous, GameSnapshot current) {
        List<BridgeSignal> signals = new ArrayList<>();

        data.flags().forEach((flag, id) -> {
            if(becameSet(previous, current, id)) signals.add(new BridgeSignal.FlagSet(flag));
        });

        data.badges().forEach((badge, id) -> {
            if(becameSet(previous, current, id)) signals.add(new BridgeSignal.BadgeObtained(badge));
        });

        Optional<String> now = data.locationFor(current.mapGroup(), current.mapNum());
        Optional<String> before = previous == null ? Optional.empty() : data.locationFor(previous.mapGroup(), previous.mapNum());
        if(now.isPresent() && !now.equals(before)) {
            signals.add(new BridgeSignal.LocationReached(now.get()));
        }

        if(previous != null && isNewWildBattle(previous, current))
            signals.add(new BridgeSignal.WildBattleStarted(current.enemyLead()));

        if(previous != null && isNewTrainerBattle(previous, current))
            signals.add(new BridgeSignal.TrainerBattleStarted(current.enemyParty()));

        if(previous != null && current.trainerBattle() && current.enemyDefeated() && !previous.enemyDefeated())
            signals.add(new BridgeSignal.TrainerBattleWon(current.enemyParty()));

        return signals;
    }

    static boolean becameSet(GameSnapshot previou, GameSnapshot current, int flagId){
        return current.isFlagSet(flagId) && (previou == null || !previou.isFlagSet(flagId));
    }

    static boolean isNewTrainerBattle(GameSnapshot previous, GameSnapshot current)  {
        PartyMon lead = current.enemyLead();
        if(!current.trainerBattle() || lead == null || !lead.valid())
            return false;

        PartyMon before = previous.enemyLead();
        return !previous.trainerBattle() || before == null || !before.identityKey().equals(lead.identityKey());
    }

    static boolean isNewWildBattle(GameSnapshot previous, GameSnapshot current) {
        PartyMon enemy = current.enemyLead();
        if(enemy == null || !enemy.valid() || current.trainerBattle())
            return false;
        return previous.enemyLead() == null || !previous.enemyLead().identityKey().equals(enemy.identityKey());
    }

}
