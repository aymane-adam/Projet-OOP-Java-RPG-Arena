package com.junia.arena.service;

import com.junia.arena.model.Team;

import java.util.Comparator;

/**
 * A team's line in the tournament table.
 */
public class TeamStanding {

    public static final int POINTS_PER_WIN = 3;
    public static final int POINTS_PER_DRAW = 1;

    /** Points first, then number of wins, then alphabetical order to keep the ranking stable. */
    public static final Comparator<TeamStanding> RANKING_ORDER =
            Comparator.comparingInt(TeamStanding::getPoints).reversed()
                    .thenComparing(Comparator.comparingInt(TeamStanding::getWins).reversed())
                    .thenComparing(standing -> standing.getTeam().getName());

    private final Team team;
    private int wins;
    private int draws;
    private int losses;

    public TeamStanding(Team team) {
        this.team = team;
    }

    void recordWin() {
        wins++;
    }

    void recordDraw() {
        draws++;
    }

    void recordLoss() {
        losses++;
    }

    public int getPoints() {
        return wins * POINTS_PER_WIN + draws * POINTS_PER_DRAW;
    }

    public int getMatchesPlayed() {
        return wins + draws + losses;
    }

    public Team getTeam() {
        return team;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }
}
