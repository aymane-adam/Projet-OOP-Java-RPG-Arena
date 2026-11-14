package com.junia.arena.action;

import com.junia.arena.model.Character;

/**
 * Immutable outcome of one action, consumed by battle listeners (display, statistics...).
 */
public final class ActionResult {

    private final Character actor;
    private final Character target;
    private final String actionName;
    private final EffectType effectType;
    private final int amount;

    public ActionResult(Character actor, Character target, String actionName, EffectType effectType, int amount) {
        this.actor = actor;
        this.target = target;
        this.actionName = actionName;
        this.effectType = effectType;
        this.amount = amount;
    }

    public Character getActor() {
        return actor;
    }

    public Character getTarget() {
        return target;
    }

    public String getActionName() {
        return actionName;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isTargetKnockedOut() {
        return !target.isAlive();
    }
}
