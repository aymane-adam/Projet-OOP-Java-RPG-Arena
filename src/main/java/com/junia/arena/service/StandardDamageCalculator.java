package com.junia.arena.service;

import com.junia.arena.model.Character;
import com.junia.arena.util.RandomSource;

import java.util.Objects;

/**
 * damage = attack x multiplier - defense x 0.5, with a chance of critical hit,
 * and never less than {@value #MIN_DAMAGE} so that a fight always ends.
 */
public class StandardDamageCalculator implements DamageCalculator {

    static final double DEFENSE_FACTOR = 0.5;
    static final double CRITICAL_CHANCE = 0.10;
    static final double CRITICAL_MULTIPLIER = 1.5;
    static final int MIN_DAMAGE = 1;

    private final RandomSource random;

    public StandardDamageCalculator(RandomSource random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    @Override
    public int computeDamage(Character attacker, Character defender, double multiplier) {
        if (multiplier <= 0) {
            throw new IllegalArgumentException("Multiplier must be positive: " + multiplier);
        }
        double damage = attacker.getAttack() * multiplier - defender.getDefense() * DEFENSE_FACTOR;
        if (random.nextDouble() < CRITICAL_CHANCE) {
            damage *= CRITICAL_MULTIPLIER;
        }
        return Math.max(MIN_DAMAGE, (int) Math.round(damage));
    }
}
