package com.junia.arena.model;

import com.junia.arena.exception.InvalidTeamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamTest {

    private Team team;
    private Character arthur;
    private Character merlin;

    @BeforeEach
    void setUp() {
        team = new Team("Les Lions");
        arthur = new Warrior("Arthur", 100, 20, 10, 5);
        merlin = new Mage("Merlin", 50, 30, 5, 10, 60);
        team.addMember(arthur);
        team.addMember(merlin);
    }

    @Test
    void shouldOnlyReturnAliveMembers() {
        merlin.takeDamage(50);

        assertEquals(List.of(arthur), team.getAliveMembers());
        assertFalse(team.isDefeated());
    }

    @Test
    void shouldBeDefeatedWhenEveryMemberIsKnockedOut() {
        arthur.takeDamage(100);
        merlin.takeDamage(50);

        assertTrue(team.isDefeated());
    }

    @Test
    void shouldComputeRemainingHealthRatio() {
        arthur.takeDamage(50);
        merlin.takeDamage(25);

        assertEquals(75.0 / 150.0, team.getRemainingHealthRatio(), 0.0001);
    }

    @Test
    void shouldRejectDuplicateMemberNameIgnoringCase() {
        assertThrows(InvalidTeamException.class, () -> team.addMember(new Warrior("ARTHUR", 80, 10, 10, 5)));
    }

    @Test
    void shouldRejectMemberWhenTeamIsFull() {
        for (int i = team.getMembers().size(); i < Team.MAX_SIZE; i++) {
            team.addMember(new Warrior("Extra" + i, 80, 10, 10, 5));
        }

        assertThrows(InvalidTeamException.class, () -> team.addMember(new Warrior("OneTooMany", 80, 10, 10, 5)));
    }

    @Test
    void shouldNotExposeAModifiableMemberList() {
        assertThrows(UnsupportedOperationException.class, () -> team.getMembers().clear());
    }

    @Test
    void shouldRestoreAllMembers() {
        arthur.takeDamage(100);
        merlin.takeDamage(10);

        team.restoreAll();

        assertEquals(150, team.getTotalHealth());
    }

    @Test
    void teamsWithSameNameShouldBeEqual() {
        assertEquals(team, new Team("les lions"));
        assertEquals(team.hashCode(), new Team("les lions").hashCode());
    }
}
