package com.junia.arena.model;

/**
 * Simple Factory: the only place that knows which concrete class matches each {@link CharacterType}.
 * Adding a new class to the game means adding an enum value and one line here,
 * the loader and the battle engine stay untouched.
 */
public class CharacterFactory {

    public Character create(CharacterType type, String name, int health, int attack,
                            int defense, int speed, int mana) {
        return switch (type) {
            case WARRIOR -> new Warrior(name, health, attack, defense, speed);
            case MAGE -> new Mage(name, health, attack, defense, speed, mana);
            case HEALER -> new Healer(name, health, attack, defense, speed, mana);
        };
    }
}
