package com.junia.arena.model;

import com.junia.arena.exception.InvalidCharacterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharacterTest {

    private Character warrior;

    @BeforeEach
    void setUp() {
        warrior = new Warrior("Arthur", 100, 20, 10, 5);
    }

    @Test
    void shouldReduceHealthWhenTakingDamage() {
        warrior.takeDamage(30);

        assertEquals(70, warrior.getHealth());
        assertTrue(warrior.isAlive());
    }

    @Test
    void shouldNeverGoBelowZeroHealth() {
        warrior.takeDamage(500);

        assertEquals(0, warrior.getHealth());
        assertFalse(warrior.isAlive());
    }

    @Test
    void shouldBeKnockedOutWhenDamageEqualsHealth() {
        warrior.takeDamage(100);

        assertFalse(warrior.isAlive());
    }

    @Test
    void shouldThrowWhenDamageIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> warrior.takeDamage(-5));
    }

    @Test
    void shouldNotHealAboveMaxHealth() {
        warrior.takeDamage(10);

        int restored = warrior.heal(50);

        assertEquals(10, restored);
        assertEquals(100, warrior.getHealth());
    }

    @Test
    void shouldThrowWhenHealingAKnockedOutCharacter() {
        warrior.takeDamage(100);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> warrior.heal(10));
        assertTrue(exception.getMessage().contains("Arthur"));
    }

    @Test
    void shouldRestoreFullHealth() {
        warrior.takeDamage(100);

        warrior.restore();

        assertEquals(100, warrior.getHealth());
        assertTrue(warrior.isAlive());
    }

    @Test
    void shouldComputeHealthRatio() {
        warrior.takeDamage(25);

        assertEquals(0.75, warrior.getHealthRatio(), 0.0001);
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(InvalidCharacterException.class, () -> new Warrior("  ", 100, 10, 10, 5));
    }

    @Test
    void shouldRejectNonPositiveHealth() {
        assertThrows(InvalidCharacterException.class, () -> new Warrior("Bob", 0, 10, 10, 5));
    }

    @Test
    void shouldRejectNegativeAttack() {
        assertThrows(InvalidCharacterException.class, () -> new Mage("Bob", 50, -1, 10, 5, 10));
    }

    @Test
    void shouldRejectNonPositiveSpeed() {
        assertThrows(InvalidCharacterException.class, () -> new Healer("Bob", 50, 10, 10, 0, 10));
    }

    @Test
    void shouldRejectNegativeMana() {
        assertThrows(InvalidCharacterException.class, () -> new Mage("Bob", 50, 10, 10, 5, -10));
    }
}
