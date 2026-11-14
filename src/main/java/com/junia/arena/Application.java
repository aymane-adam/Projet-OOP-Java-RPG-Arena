package com.junia.arena;

import com.junia.arena.exception.DataLoadingException;
import com.junia.arena.model.CharacterFactory;
import com.junia.arena.model.Team;
import com.junia.arena.repository.CsvTeamRepository;
import com.junia.arena.repository.TeamRepository;
import com.junia.arena.service.DamageCalculator;
import com.junia.arena.service.StandardDamageCalculator;
import com.junia.arena.ui.ConsoleMenu;
import com.junia.arena.util.JavaRandomSource;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point. Its only job is to assemble the objects (composition root)
 * and to stop cleanly if the data cannot be loaded.
 */
public class Application {

    private static final Path DEFAULT_DATA_FILE = Path.of("data", "characters.csv");

    public static void main(String[] args) {
        Path dataFile = args.length > 0 ? Path.of(args[0]) : DEFAULT_DATA_FILE;
        TeamRepository repository = new CsvTeamRepository(dataFile, new CharacterFactory());

        List<Team> teams;
        try {
            teams = repository.loadTeams();
        } catch (DataLoadingException e) {
            System.err.println("Impossible de démarrer le jeu : " + e.getMessage());
            System.exit(1);
            return;
        }

        System.out.println(teams.size() + " équipes chargées depuis " + dataFile);
        DamageCalculator calculator = new StandardDamageCalculator(new JavaRandomSource());
        new ConsoleMenu(teams, calculator, new Scanner(System.in), System.out).run();
    }
}
