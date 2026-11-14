package com.junia.arena.model;

import com.junia.arena.exception.InvalidCharacterException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Base class of every fighter.
 * <p>
 * Abstract because a "generic character" does not exist in the game: every
 * fighter belongs to a class that decides how it behaves during its turn.
 * The shared state (health, attack, defense, speed) and the rules that apply
 * to everybody (taking damage, being healed) live here, so subclasses only
 * describe what makes them different.
 */
public abstract class Character {

    private final String name;
    private final int maxHealth;
    private final int attack;
    private final int defense;
    private final int speed;
    private int health;

    protected Character(String name, int maxHealth, int attack, int defense, int speed) {
        if (name == null || name.isBlank()) {
            throw new InvalidCharacterException("A character must have a name");
        }
        if (maxHealth <= 0) {
            throw new InvalidCharacterException(name + ": health must be positive (was " + maxHealth + ")");
        }
        if (attack < 0 || defense < 0) {
            throw new InvalidCharacterException(name + ": attack and defense cannot be negative");
        }
        if (speed <= 0) {
            throw new InvalidCharacterException(name + ": speed must be positive (was " + speed + ")");
        }
        this.name = name.trim();
        this.maxHealth = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.health = maxHealth;
    }

    /**
     * Decides what this character does during its turn.
     *
     * @param allies  living members of its own team (including itself)
     * @param enemies living members of the opposing team, never empty
     */
    public abstract TurnDecision decideTurn(List<Character> allies, List<Character> enemies);

    public abstract CharacterType getType();

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative: " + amount);
        }
        health = Math.max(0, health - amount);
    }

    /**
     * Restores health without exceeding the maximum.
     *
     * @return the amount of health actually restored
     */
    public int heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative: " + amount);
        }
        if (!isAlive()) {
            throw new IllegalStateException(name + " is knocked out and cannot be healed");
        }
        int restored = Math.min(amount, maxHealth - health);
        health += restored;
        return restored;
    }

    /**
     * Called once at the end of every round. Does nothing by default;
     * subclasses override it when they recover a resource over time.
     */
    public void onRoundEnd() {
    }

    /**
     * Puts the character back in its initial state before a new battle.
     */
    public void restore() {
        health = maxHealth;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public double getHealthRatio() {
        return (double) health / maxHealth;
    }

    /**
     * Default targeting rule shared by all classes: finish off the weakest enemy first,
     * because removing an opponent removes its future turns.
     */
    protected Character weakestOf(List<Character> candidates) {
        return candidates.stream()
                .filter(Character::isAlive)
                .min(Comparator.comparingInt(Character::getHealth))
                .orElseThrow(() -> new IllegalStateException(name + " has no target left"));
    }

    /**
     * @return the most injured living ally below the given health ratio, if any
     */
    protected Optional<Character> mostInjuredBelow(List<Character> allies, double ratio) {
        return allies.stream()
                .filter(Character::isAlive)
                .filter(ally -> ally.getHealthRatio() < ratio)
                .min(Comparator.comparingDouble(Character::getHealthRatio));
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return String.format("%-8s %-9s PV %3d/%-3d ATK %2d DEF %2d VIT %2d",
                name, getType().getDisplayName(), health, maxHealth, attack, defense, speed);
    }
}
