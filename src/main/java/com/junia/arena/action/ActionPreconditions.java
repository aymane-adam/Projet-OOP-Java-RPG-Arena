package com.junia.arena.action;

import com.junia.arena.model.Character;

import java.util.Objects;

/**
 * Rules every action must respect, whatever its effect.
 */
final class ActionPreconditions {

    private ActionPreconditions() {
    }

    static void check(Character actor, Character target) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(target, "target");
        if (!actor.isAlive()) {
            throw new IllegalStateException(actor.getName() + " is knocked out and cannot act");
        }
        if (!target.isAlive()) {
            throw new IllegalArgumentException(target.getName() + " is already knocked out");
        }
    }
}
