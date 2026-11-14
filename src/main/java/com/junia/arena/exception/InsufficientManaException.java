package com.junia.arena.exception;

/**
 * Thrown when a spell caster tries to cast a spell it cannot afford.
 */
public class InsufficientManaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int required;
    private final int available;

    public InsufficientManaException(String casterName, int required, int available) {
        super(casterName + " needs " + required + " mana but only has " + available);
        this.required = required;
        this.available = available;
    }

    public int getRequired() {
        return required;
    }

    public int getAvailable() {
        return available;
    }
}
