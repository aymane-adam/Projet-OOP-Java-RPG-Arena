package com.junia.arena.ui;

import com.junia.arena.service.BattleListener;
import com.junia.arena.service.BattleResult;

import java.io.PrintStream;

/**
 * Prints one line per finished battle. Used during tournaments, where the
 * detailed turn-by-turn log would be far too long.
 */
public class MatchSummaryView implements BattleListener {

    private final PrintStream out;

    public MatchSummaryView(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onBattleEnd(BattleResult result) {
        out.println("  " + ResultFormatter.describe(result));
    }
}
