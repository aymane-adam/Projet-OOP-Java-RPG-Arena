package com.junia.arena.model;

import com.junia.arena.action.Action;
import com.junia.arena.action.BasicAttack;
import com.junia.arena.action.PowerStrike;

import java.util.List;

/**
 * Melee fighter. When badly hurt it enters a rage and hits much harder.
 */
public class Warrior extends Character {

    /** Below this health ratio the warrior switches to {@link PowerStrike}. */
    static final double RAGE_THRESHOLD = 0.5;

    private final Action basicAttack = new BasicAttack();
    private final Action powerStrike = new PowerStrike();

    public Warrior(String name, int maxHealth, int attack, int defense, int speed) {
        super(name, maxHealth, attack, defense, speed);
    }

    @Override
    public TurnDecision decideTurn(List<Character> allies, List<Character> enemies) {
        Action action = getHealthRatio() < RAGE_THRESHOLD ? powerStrike : basicAttack;
        return new TurnDecision(action, weakestOf(enemies));
    }

    @Override
    public CharacterType getType() {
        return CharacterType.WARRIOR;
    }
}
