package com.junia.arena.ui;

import com.junia.arena.exception.InvalidBattleException;
import com.junia.arena.model.Team;
import com.junia.arena.service.BattleEngine;
import com.junia.arena.service.BattleStatistics;
import com.junia.arena.service.DamageCalculator;
import com.junia.arena.service.TeamStanding;
import com.junia.arena.service.TournamentService;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * User interaction only: reads choices, delegates the work to the services
 * and prints what they return.
 */
public class ConsoleMenu {

    private final List<Team> teams;
    private final DamageCalculator calculator;
    private final Scanner input;
    private final PrintStream out;

    public ConsoleMenu(List<Team> teams, DamageCalculator calculator, Scanner input, PrintStream out) {
        this.teams = List.copyOf(teams);
        this.calculator = calculator;
        this.input = input;
        this.out = out;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine();
            switch (choice) {
                case "1" -> showTeams();
                case "2" -> runDuel();
                case "3" -> runTournament();
                case "0", "" -> running = false;
                default -> out.println("Choix invalide : " + choice);
            }
        }
        out.println("A bientôt dans l'arène !");
    }

    private void printMenu() {
        out.println();
        out.println("===== ARENE RPG =====");
        out.println("1. Afficher les équipes");
        out.println("2. Duel détaillé entre deux équipes");
        out.println("3. Tournoi (toutes les équipes)");
        out.println("0. Quitter");
        out.print("Votre choix : ");
    }

    private void showTeams() {
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            out.println();
            out.println((i + 1) + ". " + team.getName());
            team.getMembers().forEach(member -> out.println("     " + member));
        }
    }

    private void runDuel() {
        showTeams();
        Optional<Team> first = askTeam("Première équipe (numéro) : ");
        Optional<Team> second = first.isPresent() ? askTeam("Deuxième équipe (numéro) : ") : Optional.empty();
        if (first.isEmpty() || second.isEmpty()) {
            return;
        }

        BattleEngine engine = new BattleEngine(calculator, List.of(new ConsoleBattleView(out)));
        try {
            first.get().restoreAll();
            second.get().restoreAll();
            engine.fight(first.get(), second.get());
        } catch (InvalidBattleException e) {
            out.println("Duel impossible : " + e.getMessage());
        } finally {
            first.get().restoreAll();
            second.get().restoreAll();
        }
    }

    private void runTournament() {
        BattleStatistics statistics = new BattleStatistics();
        BattleEngine engine = new BattleEngine(calculator, List.of(new MatchSummaryView(out), statistics));
        TournamentService tournament = new TournamentService(engine);

        out.println();
        out.println("Matchs :");
        try {
            List<TeamStanding> ranking = tournament.runRoundRobin(teams);
            printRanking(ranking);
            printAwards(statistics);
        } catch (InvalidBattleException e) {
            out.println("Tournoi impossible : " + e.getMessage());
        }
    }

    private void printRanking(List<TeamStanding> ranking) {
        out.println();
        out.println("Classement :");
        out.printf("  %-4s %-15s %4s %3s %3s %3s %4s%n", "Pos", "Equipe", "J", "V", "N", "D", "Pts");
        for (int i = 0; i < ranking.size(); i++) {
            TeamStanding s = ranking.get(i);
            out.printf("  %-4d %-15s %4d %3d %3d %3d %4d%n", i + 1, s.getTeam().getName(),
                    s.getMatchesPlayed(), s.getWins(), s.getDraws(), s.getLosses(), s.getPoints());
        }
    }

    private void printAwards(BattleStatistics statistics) {
        out.println();
        out.println("Récompenses :");
        printAward("Plus gros cogneur", statistics.getTopDamageDealer(), "dégâts");
        printAward("Meilleur soigneur", statistics.getTopHealer(), "PV soignés");
        printAward("Finisseur", statistics.getTopFinisher(), "K.O.");
    }

    private void printAward(String title, Optional<Map.Entry<String, Integer>> winner, String unit) {
        String text = winner
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " " + unit + ")")
                .orElse("personne");
        out.println("  " + title + " : " + text);
    }

    /**
     * @return the chosen team, or empty if the input was not a valid team number
     */
    private Optional<Team> askTeam(String prompt) {
        out.print(prompt);
        String value = readLine();
        try {
            int number = Integer.parseInt(value);
            if (number < 1 || number > teams.size()) {
                out.println("Il n'y a pas d'équipe numéro " + number + ".");
                return Optional.empty();
            }
            return Optional.of(teams.get(number - 1));
        } catch (NumberFormatException e) {
            out.println("'" + value + "' n'est pas un numéro valide.");
            return Optional.empty();
        }
    }

    /** End of input (e.g. Ctrl+D or a piped file) is treated like an empty answer. */
    private String readLine() {
        return input.hasNextLine() ? input.nextLine().trim() : "";
    }
}
