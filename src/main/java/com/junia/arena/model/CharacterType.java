package com.junia.arena.model;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The character classes available in the game.
 */
public enum CharacterType {
    WARRIOR("Guerrier"),
    MAGE("Mage"),
    HEALER("Soigneur");

    private final String displayName;

    CharacterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Case-insensitive lookup used when parsing the data file.
     *
     * @throws IllegalArgumentException if the value matches no type
     */
    public static CharacterType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Character type is missing");
        }
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown character type '" + value + "'. Expected one of: "
                                + Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "))));
    }
}
