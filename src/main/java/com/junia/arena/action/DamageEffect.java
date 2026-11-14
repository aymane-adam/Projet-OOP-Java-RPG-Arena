package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.service.DamageCalculator;

/**
 * Damage logic shared by every offensive action, so the validation and the
 * damage application are written once instead of in each action.
 */
final class DamageEffect {

    private DamageEffect() {
    }

    static ActionResult apply(Action action, Character actor, Character target,
                              DamageCalculator calculator, double multiplier) {
        ActionPreconditions.check(actor, target);
        if (actor == target) {
            throw new IllegalArgumentException(actor.getName() + " cannot attack itself");
        }
        int damage = calculator.computeDamage(actor, target, multiplier);
        target.takeDamage(damage);
        return new ActionResult(actor, target, action.getName(), EffectType.DAMAGE, damage);
    }
}
