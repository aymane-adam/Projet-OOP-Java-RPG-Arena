package com.junia.arena.action;

import com.junia.arena.model.Character;
import com.junia.arena.service.DamageCalculator;

/**
 * Something a character can do during its turn.
 * <p>
 * An interface rather than an abstract class: a basic attack and a healing spell
 * share no state, only the contract "apply an effect from an actor to a target".
 * New actions can be added without touching the battle engine (Open/Closed).
 */
public interface Action {

    String getName();

    ActionResult execute(Character actor, Character target, DamageCalculator calculator);
}
