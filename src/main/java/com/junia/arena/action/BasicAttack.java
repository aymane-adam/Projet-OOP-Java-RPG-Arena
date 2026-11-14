package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.service.DamageCalculator;

public class BasicAttack implements Action {

    private static final double MULTIPLIER = 1.0;

    @Override
    public String getName() {
        return "Attaque";
    }

    @Override
    public ActionResult execute(Character actor, Character target, DamageCalculator calculator) {
        return DamageEffect.apply(this, actor, target, calculator, MULTIPLIER);
    }
}
