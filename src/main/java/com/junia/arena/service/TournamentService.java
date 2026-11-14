package com.junia.arena.service;

import com.junia.arena.exception.InvalidBattleException;
import com.junia.arena.model.Team;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Round-robin tournament: every team fights every other team once,
 * then teams are ranked by points.
 */
public class TournamentService {

    private final BattleEngine engine;

    public TournamentService(BattleEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    /**
     * @return the final ranking, best team first
     */
    public List<TeamStanding> runRoundRobin(List<Team> teams) {
        validate(teams);

        Map<Team, TeamStanding> standings = new LinkedHashMap<>();
        teams.forEach(team -> standings.put(team, new TeamStanding(team)));

        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Team first = teams.get(i);
                Team second = teams.get(j);
                // Each match starts with fresh characters, as in a real tournament.
                first.restoreAll();
                second.restoreAll();
                record(engine.fight(first, second), standings);
            }
        }

        teams.forEach(Team::restoreAll);
        return standings.values().stream()
                .sorted(TeamStanding.RANKING_ORDER)
                .toList();
    }

    private void validate(List<Team> teams) {
        Objects.requireNonNull(teams, "teams");
        if (teams.size() < 2) {
            throw new InvalidBattleException("A tournament needs at least 2 teams (got " + teams.size() + ")");
        }
        Set<Team> unique = new HashSet<>(teams);
        if (unique.size() != teams.size()) {
            throw new InvalidBattleException("Each team can only be registered once in a tournament");
        }
    }

    private void record(BattleResult result, Map<Team, TeamStanding> standings) {
        if (result.isDraw()) {
            standings.get(result.getFirstTeam()).recordDraw();
            standings.get(result.getSecondTeam()).recordDraw();
            return;
        }
        result.getWinner().ifPresent(winner -> standings.get(winner).recordWin());
        result.getLoser().ifPresent(loser -> standings.get(loser).recordLoss());
    }
}
