package com.junia.arena.exception;

/**
 * Thrown when a battle or a tournament cannot take place
 * (a team fighting itself, a team with nobody able to fight, not enough teams...).
 */
public class InvalidBattleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidBattleException(String message) {
        super(message);
    }
}
