package com.junia.arena.service;

import com.junia.arena.action.ActionResult;
import com.junia.arena.exception.InvalidBattleException;
import com.junia.arena.model.Character;
import com.junia.arena.model.Team;
import com.junia.arena.model.Warrior;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleEngineTest {

    private final DamageCalculator noCritical = new StandardDamageCalculator(() -> 0.99);

    /** Records every action so tests can check who acted and in which order. */
    private static class RecordingListener implements BattleListener {
        final List<String> actors = new ArrayList<>();
        BattleResult finalResult;

        @Override
        public void onAction(ActionResult result) {
            actors.add(result.getActor().getName());
        }

        @Override
        public void onBattleEnd(BattleResult result) {
            finalResult = result;
        }
    }

    private static Team teamOf(String name, Character... members) {
        Team team = new Team(name);
        for (Character member : members) {
            team.addMember(member);
        }
        return team;
    }

    @Test
    void strongerTeamShouldWin() {
        Team strong = teamOf("Strong", new Warrior("Giant", 300, 50, 10, 5));
        Team weak = teamOf("Weak", new Warrior("Rat", 30, 5, 2, 3));
        BattleEngine engine = new BattleEngine(noCritical, List.of());

        BattleResult result = engine.fight(strong, weak);

        assertEquals(strong, result.getWinner().orElseThrow());
        assertEquals(weak, result.getLoser().orElseThrow());
        assertTrue(weak.isDefeated());
        assertFalse(result.isDecidedByTimeLimit());
    }

    @Test
    void fasterCharacterShouldActFirst() {
        RecordingListener listener = new RecordingListener();
        Team slow = teamOf("Slow", new Warrior("Turtle", 100, 10, 5, 1));
        Team fast = teamOf("Fast", new Warrior("Hare", 100, 10, 5, 20));
        BattleEngine engine = new BattleEngine(noCritical, List.of(listener), 1);

        engine.fight(slow, fast);

        assertEquals(List.of("Hare", "Turtle"), listener.actors);
    }

    @Test
    void characterKnockedOutBeforeItsTurnShouldNotAct() {
        RecordingListener listener = new RecordingListener();
        Team killer = teamOf("Killer", new Warrior("Assassin", 100, 100, 5, 20));
        Team victim = teamOf("Victim", new Warrior("Slowpoke", 10, 50, 0, 1));
        BattleEngine engine = new BattleEngine(noCritical, List.of(listener));

        BattleResult result = engine.fight(killer, victim);

        assertEquals(List.of("Assassin"), listener.actors);
        assertEquals(1, result.getRounds());
    }

    @Test
    void shouldDecideByRemainingHealthWhenRoundLimitIsReached() {
        DamageCalculator onePoint = (attacker, defender, multiplier) -> 1;
        Team sturdy = teamOf("Sturdy", new Warrior("Wall", 100, 10, 5, 5));
        Team fragile = teamOf("Fragile", new Warrior("Glass", 50, 10, 5, 4));
        BattleEngine engine = new BattleEngine(onePoint, List.of(), 3);

        BattleResult result = engine.fight(sturdy, fragile);

        // Both lost 3 HP: 97% left for Sturdy, 94% for Fragile.
        assertTrue(result.isDecidedByTimeLimit());
        assertEquals(3, result.getRounds());
        assertEquals(sturdy, result.getWinner().orElseThrow());
    }

    @Test
    void shouldBeADrawWhenRoundLimitIsReachedWithEqualHealth() {
        DamageCalculator onePoint = (attacker, defender, multiplier) -> 1;
        Team left = teamOf("Left", new Warrior("A", 100, 10, 5, 5));
        Team right = teamOf("Right", new Warrior("B", 100, 10, 5, 4));
        BattleEngine engine = new BattleEngine(onePoint, List.of(), 2);

        BattleResult result = engine.fight(left, right);

        assertTrue(result.isDraw());
        assertTrue(result.getLoser().isEmpty());
    }

    @Test
    void shouldNotifyListenersAtTheEnd() {
        RecordingListener listener = new RecordingListener();
        Team strong = teamOf("Strong", new Warrior("Giant", 300, 50, 10, 5));
        Team weak = teamOf("Weak", new Warrior("Rat", 30, 5, 2, 3));

        BattleResult result = new BattleEngine(noCritical, List.of(listener)).fight(strong, weak);

        assertEquals(result, listener.finalResult);
    }

    @Test
    void shouldRejectATeamFightingItself() {
        Team team = teamOf("Solo", new Warrior("A", 100, 10, 5, 5));
        BattleEngine engine = new BattleEngine(noCritical, List.of());

        assertThrows(InvalidBattleException.class, () -> engine.fight(team, team));
    }

    @Test
    void shouldRejectATeamWithNobodyAbleToFight() {
        Character knockedOut = new Warrior("Ghost", 100, 10, 5, 5);
        knockedOut.takeDamage(100);
        Team ghosts = teamOf("Ghosts", knockedOut);
        Team alive = teamOf("Alive", new Warrior("A", 100, 10, 5, 5));
        BattleEngine engine = new BattleEngine(noCritical, List.of());

        assertThrows(InvalidBattleException.class, () -> engine.fight(alive, ghosts));
    }

    @Test
    void shouldRejectEmptyTeam() {
        Team empty = new Team("Empty");
        Team alive = teamOf("Alive", new Warrior("A", 100, 10, 5, 5));
        BattleEngine engine = new BattleEngine(noCritical, List.of());

        assertThrows(InvalidBattleException.class, () -> engine.fight(empty, alive));
    }

    @Test
    void shouldRejectNonPositiveRoundLimit() {
        assertThrows(IllegalArgumentException.class, () -> new BattleEngine(noCritical, List.of(), 0));
    }
}
