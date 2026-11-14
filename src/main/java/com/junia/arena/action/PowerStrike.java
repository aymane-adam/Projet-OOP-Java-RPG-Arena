package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.service.DamageCalculator;

/**
 * Heavy blow used by an enraged warrior.
 */
public class PowerStrike implements Action {

    static final double MULTIPLIER = 1.6;

    @Override
    public String getName() {
        return "Frappe puissante";
    }

    @Override
    public ActionResult execute(Character actor, Character target, DamageCalculator calculator) {
        return DamageEffect.apply(this, actor, target, calculator, MULTIPLIER);
    }
}
