package com.junia.arena.model;

import com.junia.arena.exception.InsufficientManaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellCasterTest {

    private final SpellCaster mage = new Mage("Merlin", 80, 30, 5, 10, 60);

    @Test
    void shouldStartWithFullMana() {
        assertEquals(60, mage.getMana());
    }

    @Test
    void shouldSpendMana() {
        mage.spendMana(25);

        assertEquals(35, mage.getMana());
    }

    @Test
    void shouldAllowSpendingExactlyAllMana() {
        mage.spendMana(60);

        assertEquals(0, mage.getMana());
        assertFalse(mage.canAfford(1));
    }

    @Test
    void shouldThrowWhenManaIsInsufficient() {
        mage.spendMana(50);

        InsufficientManaException exception =
                assertThrows(InsufficientManaException.class, () -> mage.spendMana(20));

        assertEquals(20, exception.getRequired());
        assertEquals(10, exception.getAvailable());
        assertEquals(10, mage.getMana(), "a failed cast must not consume mana");
    }

    @Test
    void shouldThrowWhenCostIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> mage.spendMana(-1));
    }

    @Test
    void shouldRegenerateTenPercentOfMaxManaAtRoundEnd() {
        mage.spendMana(30);

        mage.onRoundEnd();

        assertEquals(36, mage.getMana());
    }

    @Test
    void shouldNotRegenerateAboveMaxMana() {
        mage.spendMana(2);

        mage.regenerateMana();

        assertEquals(60, mage.getMana());
    }

    @Test
    void shouldNotRegenerateWhenKnockedOut() {
        mage.spendMana(30);
        mage.takeDamage(1000);

        mage.onRoundEnd();

        assertEquals(30, mage.getMana());
    }

    @Test
    void shouldRestoreHealthAndMana() {
        mage.spendMana(60);
        mage.takeDamage(50);

        mage.restore();

        assertEquals(60, mage.getMana());
        assertEquals(80, mage.getHealth());
        assertTrue(mage.isAlive());
    }
}
