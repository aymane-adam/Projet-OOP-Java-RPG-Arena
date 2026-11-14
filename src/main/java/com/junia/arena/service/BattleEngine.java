package com.junia.arena.service;

import com.junia.arena.action.ActionResult;
import com.junia.arena.exception.InvalidBattleException;
import com.junia.arena.model.Character;
import com.junia.arena.model.Team;
import com.junia.arena.model.TurnDecision;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Runs a turn-based battle between two teams.
 * <p>
 * The engine never checks the concrete type of a character: it asks each one
 * to {@link Character#decideTurn decide its turn} and executes the returned
 * action. A new character class or a new action therefore needs no change here.
 */
public class BattleEngine {

    public static final int DEFAULT_MAX_ROUNDS = 50;

    /** Fastest characters act first; the name breaks ties so the order is always reproducible. */
    private static final Comparator<Character> TURN_ORDER =
            Comparator.comparingInt(Character::getSpeed).reversed()
                    .thenComparing(Character::getName);

    private final DamageCalculator calculator;
    private final List<BattleListener> listeners;
    private final int maxRounds;

    public BattleEngine(DamageCalculator calculator, List<BattleListener> listeners) {
        this(calculator, listeners, DEFAULT_MAX_ROUNDS);
    }

    public BattleEngine(DamageCalculator calculator, List<BattleListener> listeners, int maxRounds) {
        if (maxRounds <= 0) {
            throw new IllegalArgumentException("Max rounds must be positive: " + maxRounds);
        }
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.listeners = List.copyOf(listeners);
        this.maxRounds = maxRounds;
    }

    public BattleResult fight(Team first, Team second) {
        validate(first, second);

        int round = 0;
        while (!first.isDefeated() && !second.isDefeated() && round < maxRounds) {
            round++;
            playRound(round, first, second);
        }

        BattleResult result = buildResult(first, second, round);
        listeners.forEach(listener -> listener.onBattleEnd(result));
        return result;
    }

    private void validate(Team first, Team second) {
        Objects.requireNonNull(first, "first team");
        Objects.requireNonNull(second, "second team");
        if (first.equals(second)) {
            throw new InvalidBattleException("Team " + first.getName() + " cannot fight itself");
        }
        if (first.isDefeated() || second.isDefeated()) {
            throw new InvalidBattleException("Both teams need at least one character able to fight ("
                    + first.getName() + " vs " + second.getName() + ")");
        }
    }

    private void playRound(int round, Team first, Team second) {
        listeners.forEach(listener -> listener.onRoundStart(round));

        List<Character> order = Stream.concat(first.getAliveMembers().stream(), second.getAliveMembers().stream())
                .sorted(TURN_ORDER)
                .toList();

        for (Character character : order) {
            // A character knocked out earlier in this round loses its turn.
            if (!character.isAlive()) {
                continue;
            }
            Team allies = first.contains(character) ? first : second;
            Team enemies = allies == first ? second : first;
            if (enemies.isDefeated()) {
                return;
            }
            playTurn(character, allies, enemies);
        }

        order.forEach(Character::onRoundEnd);
    }

    private void playTurn(Character character, Team allies, Team enemies) {
        TurnDecision decision = character.decideTurn(allies.getAliveMembers(), enemies.getAliveMembers());
        ActionResult result = decision.getAction().execute(character, decision.getTarget(), calculator);
        listeners.forEach(listener -> listener.onAction(result));
    }

    /**
     * When the round limit is reached, the team that kept the larger share of its health wins.
     * Exactly equal shares give a draw.
     */
    private BattleResult buildResult(Team first, Team second, int rounds) {
        if (second.isDefeated()) {
            return new BattleResult(first, second, first, rounds, false);
        }
        if (first.isDefeated()) {
            return new BattleResult(first, second, second, rounds, false);
        }
        int comparison = Double.compare(first.getRemainingHealthRatio(), second.getRemainingHealthRatio());
        Team winner = comparison > 0 ? first : comparison < 0 ? second : null;
        return new BattleResult(first, second, winner, rounds, true);
    }
}
