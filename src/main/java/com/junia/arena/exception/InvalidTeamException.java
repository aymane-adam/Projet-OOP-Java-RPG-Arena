package com.junia.arena.exception;

/**
 * Thrown when a team composition breaks the rules (team full, duplicate member...).
 */
public class InvalidTeamException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTeamException(String message) {
        super(message);
    }
}
