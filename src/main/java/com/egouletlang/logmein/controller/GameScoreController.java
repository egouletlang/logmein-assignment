package com.egouletlang.logmein.controller;

import com.egouletlang.logmein.db.GameRepository;
import com.egouletlang.logmein.db.PlayerRepository;
import com.egouletlang.logmein.model.Dealer;
import com.egouletlang.logmein.model.Game;
import com.egouletlang.logmein.model.Player;
import com.egouletlang.logmein.model.score.Score;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(value="Game Score", tags = { "Game Score" }, position = 4)
@SwaggerDefinition(tags = { @Tag(name = "Game Score", description = "Game Score tracker") })
@RestController
@RequestMapping(path = "games/{id}/score")
public class GameScoreController extends BaseController {

    public GameScoreController(GameRepository gameRepository, PlayerRepository playerRepository) {
        super(gameRepository, playerRepository);
    }

    // Game.Deck Resource API
    @ApiOperation(value = "View current game score", response = Score.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Current game deck. It can be empty"),
            @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetGameScoreRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Score> scores = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : game.getPlayers().entrySet()) {
            Player player = this.playerRepository.findById(entry.getKey()).orElse(null);

            if (player == null) {
                continue;
            }

            List<Integer> cards = entry.getValue();
            scores.add(new Score(player, cards, Dealer.getTotal(cards)));
        }

        Collections.sort(scores, Comparator.comparing(Score::getScore).reversed());
        return new ResponseEntity<>(scores, HttpStatus.OK);
    }


}