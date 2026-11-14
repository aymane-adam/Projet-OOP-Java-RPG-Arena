package com.junia.arena.service;

import com.junia.arena.model.Team;

import java.util.Optional;

public final class BattleResult {

    private final Team firstTeam;
    private final Team secondTeam;
    private final Team winner;
    private final int rounds;
    private final boolean decidedByTimeLimit;

    BattleResult(Team firstTeam, Team secondTeam, Team winner, int rounds, boolean decidedByTimeLimit) {
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.winner = winner;
        this.rounds = rounds;
        this.decidedByTimeLimit = decidedByTimeLimit;
    }

    /**
     * @return the winning team, or empty for a draw
     */
    public Optional<Team> getWinner() {
        return Optional.ofNullable(winner);
    }

    public boolean isDraw() {
        return winner == null;
    }

    public Optional<Team> getLoser() {
        return getWinner().map(team -> team.equals(firstTeam) ? secondTeam : firstTeam);
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }

    public int getRounds() {
        return rounds;
    }

    public boolean isDecidedByTimeLimit() {
        return decidedByTimeLimit;
    }
}
