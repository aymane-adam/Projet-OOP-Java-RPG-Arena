package com.junia.arena.model;

import com.junia.arena.action.Action;
import com.junia.arena.action.BasicAttack;
import com.junia.arena.action.HealingSpell;

import java.util.List;
import java.util.Optional;

/**
 * Support character: heals the most injured ally when someone is in danger,
 * otherwise helps with a basic attack.
 */
public class Healer extends SpellCaster {

    /** An ally is considered "in danger" below this health ratio. */
    static final double DANGER_THRESHOLD = 0.5;

    private final HealingSpell healingSpell = new HealingSpell();
    private final Action basicAttack = new BasicAttack();

    public Healer(String name, int maxHealth, int attack, int defense, int speed, int maxMana) {
        super(name, maxHealth, attack, defense, speed, maxMana);
    }

    @Override
    public TurnDecision decideTurn(List<Character> allies, List<Character> enemies) {
        Optional<Character> injuredAlly = mostInjuredBelow(allies, DANGER_THRESHOLD);
        if (injuredAlly.isPresent() && canAfford(healingSpell.getManaCost())) {
            return new TurnDecision(healingSpell, injuredAlly.get());
        }
        return new TurnDecision(basicAttack, weakestOf(enemies));
    }

    @Override
    public CharacterType getType() {
        return CharacterType.HEALER;
    }
}
