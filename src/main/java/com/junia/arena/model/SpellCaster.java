package com.junia.arena.model;

import com.junia.arena.exception.InsufficientManaException;
import com.junia.arena.exception.InvalidCharacterException;

/**
 * Intermediate abstract class for every character that casts spells.
 * Mages and healers share exactly the same mana rules, so they are written once here.
 */
public abstract class SpellCaster extends Character implements ManaUser {

    /** Share of the maximum mana recovered at the end of every round. */
    private static final double MANA_REGEN_RATIO = 0.10;

    private final int maxMana;
    private int mana;

    protected SpellCaster(String name, int maxHealth, int attack, int defense, int speed, int maxMana) {
        super(name, maxHealth, attack, defense, speed);
        if (maxMana < 0) {
            throw new InvalidCharacterException(name + ": mana cannot be negative (was " + maxMana + ")");
        }
        this.maxMana = maxMana;
        this.mana = maxMana;
    }

    @Override
    public boolean canAfford(int cost) {
        return mana >= cost;
    }

    @Override
    public void spendMana(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Mana cost cannot be negative: " + cost);
        }
        if (!canAfford(cost)) {
            throw new InsufficientManaException(getName(), cost, mana);
        }
        mana -= cost;
    }

    @Override
    public void regenerateMana() {
        int regen = (int) Math.ceil(maxMana * MANA_REGEN_RATIO);
        mana = Math.min(maxMana, mana + regen);
    }

    @Override
    public void onRoundEnd() {
        if (isAlive()) {
            regenerateMana();
        }
    }

    @Override
    public void restore() {
        super.restore();
        mana = maxMana;
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" MANA %d/%d", mana, maxMana);
    }
}
