package com.junia.arena.exception;

/**
 * Thrown when a character is created with stats that break the game rules
 * (blank name, non-positive health, negative attack...).
 */
public class InvalidCharacterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidCharacterException(String message) {
        super(message);
    }
}
