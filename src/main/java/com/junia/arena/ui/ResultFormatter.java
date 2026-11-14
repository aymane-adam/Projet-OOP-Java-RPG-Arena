package com.junia.arena.ui;

import com.junia.arena.service.BattleResult;

/**
 * Turns battle results into French sentences for the console.
 */
final class ResultFormatter {

    private ResultFormatter() {
    }

    static String describe(BattleResult result) {
        String matchup = result.getFirstTeam().getName() + " vs " + result.getSecondTeam().getName();
        String outcome = result.getWinner()
                .map(winner -> winner.getName() + " gagne")
                .orElse("match nul");
        String detail = result.isDecidedByTimeLimit()
                ? " (limite de tours atteinte, décidé aux PV restants)"
                : "";
        return String.format("%s : %s en %d tours%s", matchup, outcome, result.getRounds(), detail);
    }
}
