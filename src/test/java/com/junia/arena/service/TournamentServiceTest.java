package com.junia.arena.service;

import com.junia.arena.exception.InvalidBattleException;
import com.junia.arena.model.Character;
import com.junia.arena.model.Team;
import com.junia.arena.model.Warrior;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentServiceTest {

    private final BattleEngine engine = new BattleEngine(new StandardDamageCalculator(() -> 0.99), List.of());
    private final TournamentService tournament = new TournamentService(engine);

    private static Team teamWith(String name, Character member) {
        Team team = new Team(name);
        team.addMember(member);
        return team;
    }

    @Test
    void shouldRankTeamsByPoints() {
        Team weak = teamWith("Weak", new Warrior("W", 80, 10, 5, 5));
        Team strong = teamWith("Strong", new Warrior("S", 200, 40, 10, 10));
        Team medium = teamWith("Medium", new Warrior("M", 150, 25, 10, 8));

        List<TeamStanding> ranking = tournament.runRoundRobin(List.of(weak, strong, medium));

        assertEquals(List.of("Strong", "Medium", "Weak"),
                ranking.stream().map(standing -> standing.getTeam().getName()).toList());
        assertEquals(2 * TeamStanding.POINTS_PER_WIN, ranking.get(0).getPoints());
        assertEquals(TeamStanding.POINTS_PER_WIN, ranking.get(1).getPoints());
        assertEquals(0, ranking.get(2).getPoints());
    }

    @Test
    void everyTeamShouldPlayEveryOtherTeamOnce() {
        List<Team> teams = List.of(
                teamWith("A", new Warrior("a", 100, 20, 5, 5)),
                teamWith("B", new Warrior("b", 100, 21, 5, 6)),
                teamWith("C", new Warrior("c", 100, 22, 5, 7)),
                teamWith("D", new Warrior("d", 100, 23, 5, 8)));

        List<TeamStanding> ranking = tournament.runRoundRobin(teams);

        ranking.forEach(standing -> assertEquals(3, standing.getMatchesPlayed()));
    }

    @Test
    void drawShouldGiveOnePointToEachTeam() {
        BattleEngine shortEngine = new BattleEngine((attacker, defender, multiplier) -> 1, List.of(), 1);
        Team left = teamWith("Left", new Warrior("L", 100, 10, 5, 5));
        Team right = teamWith("Right", new Warrior("R", 100, 10, 5, 4));

        List<TeamStanding> ranking = new TournamentService(shortEngine).runRoundRobin(List.of(left, right));

        ranking.forEach(standing -> {
            assertEquals(1, standing.getDraws());
            assertEquals(TeamStanding.POINTS_PER_DRAW, standing.getPoints());
        });
    }

    @Test
    void charactersShouldBeRestoredAfterTheTournament() {
        Character survivor = new Warrior("S", 200, 40, 10, 10);
        Team strong = teamWith("Strong", survivor);
        Team weak = teamWith("Weak", new Warrior("W", 80, 10, 5, 5));

        tournament.runRoundRobin(List.of(strong, weak));

        assertEquals(survivor.getMaxHealth(), survivor.getHealth());
    }

    @Test
    void shouldRequireAtLeastTwoTeams() {
        Team alone = teamWith("Alone", new Warrior("A", 100, 10, 5, 5));

        assertThrows(InvalidBattleException.class, () -> tournament.runRoundRobin(List.of(alone)));
    }

    @Test
    void shouldRejectTheSameTeamRegisteredTwice() {
        Team team = teamWith("Twice", new Warrior("A", 100, 10, 5, 5));
        Team other = teamWith("Other", new Warrior("B", 100, 10, 5, 5));

        assertThrows(InvalidBattleException.class, () -> tournament.runRoundRobin(List.of(team, other, team)));
    }
}
