package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

import java.time.Instant;

import es.vargontoc.pokemon.nuzlocker.domain.models.agents.TurnResult;

public record BridgeStatus(boolean connected, String error, String romKey, Long attachRun, String location, PendingEncounter encounter,  String currentTrainerId, TurnResult lastDecision, Instant at) {
    public static BridgeStatus disconnected(String error, Long attachRun, TurnResult result) {
        return new BridgeStatus(false, error, null, attachRun, null, null, null, result, Instant.now());
    }
}
