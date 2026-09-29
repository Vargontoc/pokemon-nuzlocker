package es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RuleReasonType;

public record RuleDecision(boolean allowed, boolean consumeZone, RuleReasonType reason, String message) {
    public static RuleDecision allow(RuleReasonType reason, boolean consumesZone, String message) {
        return new RuleDecision(true, consumesZone, reason, message);
    }

    public static RuleDecision deny(RuleReasonType reason, String message){
        return new RuleDecision(false, false, reason, message);
    }
}
