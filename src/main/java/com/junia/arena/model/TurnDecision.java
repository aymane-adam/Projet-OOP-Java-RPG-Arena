package com.junia.arena.model;

import com.junia.arena.action.Action;

import java.util.Objects;

/**
 * What a character chose to do during its turn: an action and its target.
 */
public final class TurnDecision {

    private final Action action;
    private final Character target;

    public TurnDecision(Action action, Character target) {
        this.action = Objects.requireNonNull(action, "action");
        this.target = Objects.requireNonNull(target, "target");
    }

    public Action getAction() {
        return action;
    }

    public Character getTarget() {
        return target;
    }
}
