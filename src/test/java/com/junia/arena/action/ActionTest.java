package com.junia.arena.action;

import com.junia.arena.exception.InsufficientManaException;
import com.junia.arena.model.Character;
import com.junia.arena.model.Healer;
import com.junia.arena.model.Mage;
import com.junia.arena.model.Warrior;
import com.junia.arena.service.DamageCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionTest {

    /** Returns 10 x multiplier, so each action's multiplier is directly visible in the result. */
    private final DamageCalculator calculator = (attacker, defender, multiplier) -> (int) Math.round(10 * multiplier);

    private final Warrior warrior = new Warrior("Arthur", 100, 20, 10, 5);
    private final Warrior enemy = new Warrior("Fenrir", 100, 20, 10, 5);

    @Test
    void basicAttackShouldDealDamageToTarget() {
        ActionResult result = new BasicAttack().execute(warrior, enemy, calculator);

        assertEquals(10, result.getAmount());
        assertEquals(EffectType.DAMAGE, result.getEffectType());
        assertEquals(90, enemy.getHealth());
    }

    @Test
    void powerStrikeShouldHitHarderThanBasicAttack() {
        ActionResult result = new PowerStrike().execute(warrior, enemy, calculator);

        assertEquals(16, result.getAmount());
    }

    @Test
    void shouldReportKnockout() {
        enemy.takeDamage(95);

        ActionResult result = new BasicAttack().execute(warrior, enemy, calculator);

        assertTrue(result.isTargetKnockedOut());
    }

    @Test
    void shouldNotAllowAKnockedOutCharacterToAct() {
        warrior.takeDamage(100);

        assertThrows(IllegalStateException.class, () -> new BasicAttack().execute(warrior, enemy, calculator));
    }

    @Test
    void shouldNotAttackAKnockedOutTarget() {
        enemy.takeDamage(100);

        assertThrows(IllegalArgumentException.class, () -> new BasicAttack().execute(warrior, enemy, calculator));
    }

    @Test
    void shouldNotAttackItself() {
        assertThrows(IllegalArgumentException.class, () -> new BasicAttack().execute(warrior, warrior, calculator));
    }

    @Test
    void fireballShouldConsumeManaAndDealDamage() {
        Mage mage = new Mage("Merlin", 80, 30, 5, 10, 60);

        ActionResult result = new Fireball().execute(mage, enemy, calculator);

        assertEquals(18, result.getAmount());
        assertEquals(60 - Fireball.MANA_COST, mage.getMana());
    }

    @Test
    void fireballShouldFailWithoutEnoughManaAndLeaveTargetUntouched() {
        Mage mage = new Mage("Merlin", 80, 30, 5, 10, 10);

        assertThrows(InsufficientManaException.class, () -> new Fireball().execute(mage, enemy, calculator));
        assertEquals(100, enemy.getHealth());
    }

    @Test
    void spellShouldBeRejectedForCharactersWithoutMana() {
        assertThrows(IllegalArgumentException.class, () -> new Fireball().execute(warrior, enemy, calculator));
    }

    @Test
    void healingSpellShouldRestoreHealthBasedOnHealerPower() {
        Healer healer = new Healer("Viviane", 90, 10, 8, 9, 70);
        Character ally = new Warrior("Lancelot", 200, 20, 10, 5);
        ally.takeDamage(100);

        ActionResult result = new HealingSpell().execute(healer, ally, calculator);

        // 20 base + 10 attack x 1.5
        assertEquals(35, result.getAmount());
        assertEquals(EffectType.HEAL, result.getEffectType());
        assertEquals(135, ally.getHealth());
        assertEquals(70 - HealingSpell.MANA_COST, healer.getMana());
    }

    @Test
    void healingSpellShouldOnlyReportHealthActuallyRestored() {
        Healer healer = new Healer("Viviane", 90, 10, 8, 9, 70);
        Character ally = new Warrior("Lancelot", 100, 20, 10, 5);
        ally.takeDamage(5);

        ActionResult result = new HealingSpell().execute(healer, ally, calculator);

        assertEquals(5, result.getAmount());
    }

    @Test
    void healingSpellShouldNotReviveAKnockedOutAlly() {
        Healer healer = new Healer("Viviane", 90, 10, 8, 9, 70);
        Character ally = new Warrior("Lancelot", 100, 20, 10, 5);
        ally.takeDamage(100);

        assertThrows(IllegalArgumentException.class, () -> new HealingSpell().execute(healer, ally, calculator));
        assertEquals(70, healer.getMana(), "no mana should be spent on an invalid target");
    }
}
