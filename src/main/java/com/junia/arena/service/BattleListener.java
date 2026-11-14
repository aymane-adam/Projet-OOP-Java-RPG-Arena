package com.junia.arena.service;

import com.junia.arena.action.ActionResult;

/**
 * Observer notified of what happens during a battle.
 * <p>
 * The engine only runs the fight; displaying it or computing statistics is done
 * by listeners. Default methods let each listener implement only the events it cares about.
 */
public interface BattleListener {

    default void onRoundStart(int round) {
    }

    default void onAction(ActionResult result) {
    }

    default void onBattleEnd(BattleResult result) {
    }
}
