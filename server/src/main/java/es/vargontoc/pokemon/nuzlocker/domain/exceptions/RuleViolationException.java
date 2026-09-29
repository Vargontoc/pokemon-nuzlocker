package es.vargontoc.pokemon.nuzlocker.domain.exceptions;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;

public class RuleViolationException extends RuntimeException {
    

    public RuleViolationException(RuleDecision decision) {
        super(decision.message());
    }
    
}
