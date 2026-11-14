package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.service.DamageCalculator;

/**
 * Restores a fixed base amount plus a bonus that grows with the healer's attack stat
 * (which represents its overall power).
 */
public class HealingSpell extends Spell {

    static final int MANA_COST = 15;
    static final int BASE_HEAL = 20;
    static final double POWER_RATIO = 1.5;

    public HealingSpell() {
        super(MANA_COST);
    }

    @Override
    public String getName() {
        return "Soin";
    }

    @Override
    protected ActionResult applyEffect(Character caster, Character target, DamageCalculator calculator) {
        int potential = BASE_HEAL + (int) Math.round(caster.getAttack() * POWER_RATIO);
        int restored = target.heal(potential);
        return new ActionResult(caster, target, getName(), EffectType.HEAL, restored);
    }
}
