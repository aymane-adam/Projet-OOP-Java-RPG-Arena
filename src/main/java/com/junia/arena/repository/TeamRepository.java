package com.junia.arena.repository;

import com.junia.arena.exception.DataLoadingException;
import com.junia.arena.model.Team;

import java.util.List;

/**
 * Source of the initial teams. The application only knows this abstraction,
 * so switching from CSV to JSON or a database means writing a new implementation,
 * not changing the game.
 */
public interface TeamRepository {

    List<Team> loadTeams() throws DataLoadingException;
}
