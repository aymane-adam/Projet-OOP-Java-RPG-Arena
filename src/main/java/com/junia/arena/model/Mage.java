package com.junia.arena.model;

import com.junia.arena.action.Action;
import com.junia.arena.action.BasicAttack;
import com.junia.arena.action.Fireball;

import java.util.Comparator;
import java.util.List;

/**
 * Glass cannon: casts fireballs at the most dangerous enemy while it has mana,
 * then falls back to a weak staff attack.
 */
public class Mage extends SpellCaster {

    private final Fireball fireball = new Fireball();
    private final Action staffAttack = new BasicAttack();

    public Mage(String name, int maxHealth, int attack, int defense, int speed, int maxMana) {
        super(name, maxHealth, attack, defense, speed, maxMana);
    }

    @Override
    public TurnDecision decideTurn(List<Character> allies, List<Character> enemies) {
        if (canAfford(fireball.getManaCost())) {
            // Spells are expensive: spend them on the enemy that hits the hardest.
            Character strongest = enemies.stream()
                    .filter(Character::isAlive)
                    .max(Comparator.comparingInt(Character::getAttack))
                    .orElseThrow(() -> new IllegalStateException(getName() + " has no target left"));
            return new TurnDecision(fireball, strongest);
        }
        return new TurnDecision(staffAttack, weakestOf(enemies));
    }

    @Override
    public CharacterType getType() {
        return CharacterType.MAGE;
    }
}
