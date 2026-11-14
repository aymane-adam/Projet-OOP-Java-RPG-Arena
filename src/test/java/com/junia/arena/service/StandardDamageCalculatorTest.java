package com.junia.arena.service;

import com.junia.arena.model.Character;
import com.junia.arena.model.Warrior;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardDamageCalculatorTest {

    private static final double NO_CRITICAL = 0.99;
    private static final double CRITICAL = 0.01;

    private final Character attacker = new Warrior("Attacker", 100, 20, 10, 5);
    private final Character defender = new Warrior("Defender", 100, 20, 10, 5);

    @Test
    void shouldSubtractHalfOfDefenseFromAttack() {
        DamageCalculator calculator = new StandardDamageCalculator(() -> NO_CRITICAL);

        // 20 x 1.0 - 10 x 0.5
        assertEquals(15, calculator.computeDamage(attacker, defender, 1.0));
    }

    @Test
    void shouldApplyMultiplier() {
        DamageCalculator calculator = new StandardDamageCalculator(() -> NO_CRITICAL);

        // 20 x 2.0 - 5
        assertEquals(35, calculator.computeDamage(attacker, defender, 2.0));
    }

    @Test
    void shouldIncreaseDamageOnCriticalHit() {
        DamageCalculator calculator = new StandardDamageCalculator(() -> CRITICAL);

        // 15 x 1.5 = 22.5, rounded to 23
        assertEquals(23, calculator.computeDamage(attacker, defender, 1.0));
    }

    @Test
    void shouldAlwaysDealAtLeastMinimumDamage() {
        DamageCalculator calculator = new StandardDamageCalculator(() -> NO_CRITICAL);
        Character tank = new Warrior("Tank", 100, 1, 500, 5);

        assertEquals(StandardDamageCalculator.MIN_DAMAGE, calculator.computeDamage(attacker, tank, 1.0));
    }

    @Test
    void shouldRejectNonPositiveMultiplier() {
        DamageCalculator calculator = new StandardDamageCalculator(() -> NO_CRITICAL);

        assertThrows(IllegalArgumentException.class, () -> calculator.computeDamage(attacker, defender, 0));
    }
}
