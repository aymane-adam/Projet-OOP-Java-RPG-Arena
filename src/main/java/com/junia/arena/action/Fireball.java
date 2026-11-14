package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.service.DamageCalculator;

public class Fireball extends Spell {

    static final int MANA_COST = 20;
    static final double MULTIPLIER = 1.8;

    public Fireball() {
        super(MANA_COST);
    }

    @Override
    public String getName() {
        return "Boule de feu";
    }

    @Override
    protected ActionResult applyEffect(Character caster, Character target, DamageCalculator calculator) {
        return DamageEffect.apply(this, caster, target, calculator, MULTIPLIER);
    }
}
