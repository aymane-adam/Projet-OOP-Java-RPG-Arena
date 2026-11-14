package com.junia.arena.model;

import com.junia.arena.exception.InvalidTeamException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A named group of characters fighting together.
 */
public class Team {

    public static final int MAX_SIZE = 5;

    private final String name;
    private final List<Character> members = new ArrayList<>();

    public Team(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidTeamException("A team must have a name");
        }
        this.name = name.trim();
    }

    public void addMember(Character character) {
        Objects.requireNonNull(character, "character");
        if (members.size() >= MAX_SIZE) {
            throw new InvalidTeamException("Team " + name + " is full (" + MAX_SIZE + " members max)");
        }
        boolean nameTaken = members.stream()
                .anyMatch(member -> member.getName().equalsIgnoreCase(character.getName()));
        if (nameTaken) {
            throw new InvalidTeamException("Team " + name + " already has a member named " + character.getName());
        }
        members.add(character);
    }

    public boolean contains(Character character) {
        return members.contains(character);
    }

    public List<Character> getAliveMembers() {
        return members.stream().filter(Character::isAlive).toList();
    }

    public boolean isDefeated() {
        return getAliveMembers().isEmpty();
    }

    public int getTotalHealth() {
        return members.stream().mapToInt(Character::getHealth).sum();
    }

    public int getTotalMaxHealth() {
        return members.stream().mapToInt(Character::getMaxHealth).sum();
    }

    /**
     * Share of the team's total health still remaining, between 0 and 1.
     * Used to decide a battle that reaches the round limit.
     */
    public double getRemainingHealthRatio() {
        return (double) getTotalHealth() / getTotalMaxHealth();
    }

    public void restoreAll() {
        members.forEach(Character::restore);
    }

    public String getName() {
        return name;
    }

    public List<Character> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /** Two teams are the same team if they have the same name: names are unique in a tournament. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Team)) {
            return false;
        }
        return name.equalsIgnoreCase(((Team) other).name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
