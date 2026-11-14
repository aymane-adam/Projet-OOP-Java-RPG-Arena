package com.junia.arena.repository;

import com.junia.arena.exception.DataLoadingException;
import com.junia.arena.model.CharacterFactory;
import com.junia.arena.model.Healer;
import com.junia.arena.model.Mage;
import com.junia.arena.model.Team;
import com.junia.arena.model.Warrior;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvTeamRepositoryTest {

    private static final String HEADER = "team,type,name,health,attack,defense,speed,mana";

    @TempDir
    Path tempDir;

    private CsvTeamRepository repositoryFor(String... lines) throws IOException {
        Path file = tempDir.resolve("characters.csv");
        Files.write(file, List.of(lines), StandardCharsets.UTF_8);
        return new CsvTeamRepository(file, new CharacterFactory());
    }

    @Test
    void shouldLoadTeamsInFileOrder() throws Exception {
        CsvTeamRepository repository = repositoryFor(
                "# a comment",
                HEADER,
                "Lions,WARRIOR,Arthur,140,22,14,8,",
                "Lions,mage,Merlin,80,28,5,10,60",
                "",
                "Loups,HEALER,Freya,85,9,7,12,80");

        List<Team> teams = repository.loadTeams();

        assertEquals(2, teams.size());
        assertEquals("Lions", teams.get(0).getName());
        assertEquals(2, teams.get(0).getMembers().size());
        assertInstanceOf(Warrior.class, teams.get(0).getMembers().get(0));
        assertInstanceOf(Mage.class, teams.get(0).getMembers().get(1));
        assertInstanceOf(Healer.class, teams.get(1).getMembers().get(0));
        assertEquals(80, ((Healer) teams.get(1).getMembers().get(0)).getMaxMana());
    }

    @Test
    void shouldLoadTheProvidedDataFile() throws Exception {
        CsvTeamRepository repository = new CsvTeamRepository(Path.of("data", "characters.csv"), new CharacterFactory());

        List<Team> teams = repository.loadTeams();

        assertTrue(teams.size() >= 2);
    }

    @Test
    void shouldFailWhenFileDoesNotExist() {
        CsvTeamRepository repository = new CsvTeamRepository(tempDir.resolve("missing.csv"), new CharacterFactory());

        DataLoadingException exception = assertThrows(DataLoadingException.class, repository::loadTeams);
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldFailWithLineNumberWhenColumnsAreMissing() throws Exception {
        CsvTeamRepository repository = repositoryFor(HEADER, "Lions,WARRIOR,Arthur,140");

        DataLoadingException exception = assertThrows(DataLoadingException.class, repository::loadTeams);
        assertTrue(exception.getMessage().contains("line 2"), exception.getMessage());
    }

    @Test
    void shouldFailWhenANumberIsInvalid() throws Exception {
        CsvTeamRepository repository = repositoryFor(HEADER, "Lions,WARRIOR,Arthur,lots,22,14,8,0");

        DataLoadingException exception = assertThrows(DataLoadingException.class, repository::loadTeams);
        assertTrue(exception.getMessage().contains("health"), exception.getMessage());
    }

    @Test
    void shouldFailWhenTypeIsUnknown() throws Exception {
        CsvTeamRepository repository = repositoryFor(HEADER, "Lions,DRAGON,Smaug,500,50,20,5,0");

        DataLoadingException exception = assertThrows(DataLoadingException.class, repository::loadTeams);
        assertTrue(exception.getMessage().contains("DRAGON"), exception.getMessage());
    }

    @Test
    void shouldFailWhenStatsBreakTheRules() throws Exception {
        CsvTeamRepository repository = repositoryFor(HEADER, "Lions,WARRIOR,Arthur,0,22,14,8,0");

        assertThrows(DataLoadingException.class, repository::loadTeams);
    }

    @Test
    void shouldFailWhenATeamHasTwoCharactersWithTheSameName() throws Exception {
        CsvTeamRepository repository = repositoryFor(HEADER,
                "Lions,WARRIOR,Arthur,140,22,14,8,0",
                "Lions,MAGE,Arthur,80,28,5,10,60");

        DataLoadingException exception = assertThrows(DataLoadingException.class, repository::loadTeams);
        assertTrue(exception.getMessage().contains("line 3"), exception.getMessage());
    }

    @Test
    void shouldFailWhenFileContainsNoCharacter() throws Exception {
        CsvTeamRepository repository = repositoryFor(HEADER);

        assertThrows(DataLoadingException.class, repository::loadTeams);
    }
}
