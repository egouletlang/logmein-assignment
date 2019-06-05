package com.egouletlang.logmein.controller;

import com.egouletlang.logmein.db.GameRepository;
import com.egouletlang.logmein.db.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public class BaseController {

    final GameRepository gameRepository;
    final PlayerRepository playerRepository;

    @Autowired
    public BaseController(final GameRepository gameRepository, final PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    void validate(@RequestBody Map<String, Object> body, String... keys) throws Exception {
        for (String key: keys) {
            if (!body.containsKey(key) || body.get(key) == null ) {
                throw new Exception(String.format("You must specify a value for '%s'", key));
            }
        }
    }

    void validate(String key, String value, String... candidates) throws Exception {
        for (String candidate: candidates) {
            if (candidate.equals(value)) {
                return;
            }
        }
        throw new Exception(String.format("'%s' is not a valid value for '%s", value, key));
    }


}