package com.junia.arena.model;

import com.junia.arena.action.BasicAttack;
import com.junia.arena.action.Fireball;
import com.junia.arena.action.HealingSpell;
import com.junia.arena.action.PowerStrike;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Each class reacts differently to the same situation: that is the polymorphism
 * the battle engine relies on.
 */
class CharacterBehaviorTest {

    private final Character weakEnemy = new Warrior("Weak", 100, 5, 5, 5);
    private final Character strongEnemy = new Warrior("Strong", 100, 40, 5, 5);

    CharacterBehaviorTest() {
        weakEnemy.takeDamage(60);
    }

    @Test
    void warriorShouldUseBasicAttackOnWeakestEnemyWhenHealthy() {
        Warrior warrior = new Warrior("Arthur", 100, 20, 10, 5);

        TurnDecision decision = warrior.decideTurn(List.of(warrior), List.of(strongEnemy, weakEnemy));

        assertInstanceOf(BasicAttack.class, decision.getAction());
        assertSame(weakEnemy, decision.getTarget());
    }

    @Test
    void warriorShouldUsePowerStrikeWhenBelowHalfHealth() {
        Warrior warrior = new Warrior("Arthur", 100, 20, 10, 5);
        warrior.takeDamage(51);

        TurnDecision decision = warrior.decideTurn(List.of(warrior), List.of(strongEnemy));

        assertInstanceOf(PowerStrike.class, decision.getAction());
    }

    @Test
    void mageShouldCastFireballOnStrongestEnemyWhenItHasMana() {
        Mage mage = new Mage("Merlin", 80, 30, 5, 10, 60);

        TurnDecision decision = mage.decideTurn(List.of(mage), List.of(weakEnemy, strongEnemy));

        assertInstanceOf(Fireball.class, decision.getAction());
        assertSame(strongEnemy, decision.getTarget());
    }

    @Test
    void mageShouldFallBackToBasicAttackWithoutMana() {
        Mage mage = new Mage("Merlin", 80, 30, 5, 10, 60);
        mage.spendMana(55);

        TurnDecision decision = mage.decideTurn(List.of(mage), List.of(weakEnemy, strongEnemy));

        assertInstanceOf(BasicAttack.class, decision.getAction());
        assertSame(weakEnemy, decision.getTarget());
    }

    @Test
    void healerShouldHealMostInjuredAllyInDanger() {
        Healer healer = new Healer("Viviane", 90, 10, 8, 9, 70);
        Character slightlyHurt = new Warrior("A", 100, 10, 10, 5);
        Character badlyHurt = new Warrior("B", 100, 10, 10, 5);
        slightlyHurt.takeDamage(55);
        badlyHurt.takeDamage(80);

        TurnDecision decision = healer.decideTurn(List.of(healer, slightlyHurt, badlyHurt), List.of(strongEnemy));

        assertInstanceOf(HealingSpell.class, decision.getAction());
        assertSame(badlyHurt, decision.getTarget());
    }

    @Test
    void healerShouldAttackWhenNoAllyIsInDanger() {
        Healer healer = new Healer("Viviane", 90, 10, 8, 9, 70);
        Character ally = new Warrior("A", 100, 10, 10, 5);
        ally.takeDamage(40);

        TurnDecision decision = healer.decideTurn(List.of(healer, ally), List.of(strongEnemy, weakEnemy));

        assertInstanceOf(BasicAttack.class, decision.getAction());
        assertSame(weakEnemy, decision.getTarget());
    }

    @Test
    void healerShouldAttackWhenAllyIsInDangerButManaIsTooLow() {
        Healer healer = new Healer("Viviane", 90, 10, 8, 9, 10);
        Character ally = new Warrior("A", 100, 10, 10, 5);
        ally.takeDamage(90);

        TurnDecision decision = healer.decideTurn(List.of(healer, ally), List.of(strongEnemy));

        assertInstanceOf(BasicAttack.class, decision.getAction());
    }
}
