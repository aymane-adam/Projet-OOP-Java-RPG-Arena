package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.model.ManaUser;
import com.junia.arena.service.DamageCalculator;

/**
 * Base class of every spell.
 * <p>
 * Abstract class (and not only the {@link Action} interface) because all spells
 * share state (a mana cost) and the same casting sequence: check the caster,
 * pay the mana, then apply the effect. That sequence is fixed in
 * {@link #execute} and subclasses only provide {@link #applyEffect} (Template Method).
 */
public abstract class Spell implements Action {

    private final int manaCost;

    protected Spell(int manaCost) {
        if (manaCost < 0) {
            throw new IllegalArgumentException("Mana cost cannot be negative: " + manaCost);
        }
        this.manaCost = manaCost;
    }

    @Override
    public final ActionResult execute(Character actor, Character target, DamageCalculator calculator) {
        if (!(actor instanceof ManaUser)) {
            throw new IllegalArgumentException(actor.getName() + " cannot cast spells");
        }
        ActionPreconditions.check(actor, target);
        // Pay before applying: if the caster cannot afford it, nothing happens to the target.
        ((ManaUser) actor).spendMana(manaCost);
        return applyEffect(actor, target, calculator);
    }

    protected abstract ActionResult applyEffect(Character caster, Character target, DamageCalculator calculator);

    public int getManaCost() {
        return manaCost;
    }
}
