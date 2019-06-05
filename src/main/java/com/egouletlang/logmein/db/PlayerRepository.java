package com.egouletlang.logmein.db;

import com.egouletlang.logmein.model.Player;

public interface PlayerRepository extends BaseRepository<Player> {

    Player findByUsername(String username);

}