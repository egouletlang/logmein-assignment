package com.egouletlang.logmein.db;

import com.egouletlang.logmein.model.Game;

public interface GameRepository extends BaseRepository<Game> {

	Game findByName(String name);

}