package com.junia.arena.model;

/**
 * Capability of characters that own a mana pool.
 * <p>
 * Kept as a small separate interface (Interface Segregation): warriors have no
 * mana, so they must not be forced to implement mana-related methods.
 */
public interface ManaUser {

    int getMana();

    int getMaxMana();

    boolean canAfford(int cost);

    /**
     * @throws com.junia.arena.exception.InsufficientManaException if the cost exceeds the current mana
     */
    void spendMana(int cost);

    void regenerateMana();
}
