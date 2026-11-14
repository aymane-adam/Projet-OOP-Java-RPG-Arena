package com.junia.arena.exception;

/**
 * Thrown when the initial data file cannot be read or contains invalid data.
 * Checked on purpose: the caller (the application entry point) must decide
 * what to do when the game cannot start.
 */
public class DataLoadingException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataLoadingException(String message) {
        super(message);
    }

    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
