package com.junia.arena.service;

import com.junia.arena.model.Character;

/**
 * Damage formula used by the offensive actions.
 * <p>
 * Actions depend on this abstraction rather than on a concrete formula
 * (Dependency Inversion): the formula can be rebalanced, or replaced by a
 * predictable one in tests, without touching any action.
 */
public interface DamageCalculator {

    int computeDamage(Character attacker, Character defender, double multiplier);
}
