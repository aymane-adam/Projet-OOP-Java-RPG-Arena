package com.junia.arena.repository;

import com.junia.arena.exception.DataLoadingException;
import com.junia.arena.exception.InvalidCharacterException;
import com.junia.arena.exception.InvalidTeamException;
import com.junia.arena.model.Character;
import com.junia.arena.model.CharacterFactory;
import com.junia.arena.model.CharacterType;
import com.junia.arena.model.Team;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads teams from a CSV file with the columns:
 * {@code team,type,name,health,attack,defense,speed,mana}.
 * <p>
 * Blank lines, lines starting with {@code #} and the header line are ignored.
 * Any invalid line stops the loading with the line number in the message:
 * starting a game with silently missing characters would be worse than not starting.
 */
public class CsvTeamRepository implements TeamRepository {

    private static final String SEPARATOR = ",";
    private static final String COMMENT_PREFIX = "#";
    private static final String HEADER_PREFIX = "team" + SEPARATOR;
    private static final int COLUMN_COUNT = 8;

    private final Path file;
    private final CharacterFactory factory;

    public CsvTeamRepository(Path file, CharacterFactory factory) {
        this.file = Objects.requireNonNull(file, "file");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    @Override
    public List<Team> loadTeams() throws DataLoadingException {
        List<String> lines = readLines();
        // LinkedHashMap keeps the teams in the order they appear in the file.
        Map<String, Team> teamsByName = new LinkedHashMap<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (isIgnored(line)) {
                continue;
            }
            int lineNumber = index + 1;
            try {
                parseLine(line, lineNumber, teamsByName);
            } catch (IllegalArgumentException | InvalidCharacterException | InvalidTeamException e) {
                // NumberFormatException is an IllegalArgumentException: bad numbers end up here too.
                throw new DataLoadingException(errorPrefix(lineNumber) + e.getMessage(), e);
            }
        }

        if (teamsByName.isEmpty()) {
            throw new DataLoadingException("No character found in " + file);
        }
        return new ArrayList<>(teamsByName.values());
    }

    private List<String> readLines() throws DataLoadingException {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new DataLoadingException("Data file not found: " + file.toAbsolutePath(), e);
        } catch (IOException e) {
            throw new DataLoadingException("Cannot read data file " + file + ": " + e.getMessage(), e);
        }
    }

    private boolean isIgnored(String line) {
        return line.isEmpty()
                || line.startsWith(COMMENT_PREFIX)
                || line.toLowerCase().startsWith(HEADER_PREFIX);
    }

    private void parseLine(String line, int lineNumber, Map<String, Team> teamsByName) throws DataLoadingException {
        String[] columns = line.split(SEPARATOR, -1);
        if (columns.length != COLUMN_COUNT) {
            throw new DataLoadingException(errorPrefix(lineNumber) + "expected " + COLUMN_COUNT
                    + " columns but found " + columns.length);
        }

        String teamName = columns[0].trim();
        CharacterType type = CharacterType.fromString(columns[1]);
        String name = columns[2].trim();
        int health = parseNumber(columns[3], "health");
        int attack = parseNumber(columns[4], "attack");
        int defense = parseNumber(columns[5], "defense");
        int speed = parseNumber(columns[6], "speed");
        // Mana is optional: warriors usually leave it empty.
        int mana = columns[7].isBlank() ? 0 : parseNumber(columns[7], "mana");

        Character character = factory.create(type, name, health, attack, defense, speed, mana);
        teamsByName.computeIfAbsent(teamName.toLowerCase(), key -> new Team(teamName)).addMember(character);
    }

    private int parseNumber(String value, String field) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + value.trim() + "' is not a valid " + field);
        }
    }

    private String errorPrefix(int lineNumber) {
        return file.getFileName() + ", line " + lineNumber + ": ";
    }
}
