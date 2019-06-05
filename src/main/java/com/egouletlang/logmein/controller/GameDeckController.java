package com.egouletlang.logmein.controller;

import com.egouletlang.logmein.db.GameRepository;
import com.egouletlang.logmein.db.PlayerRepository;
import com.egouletlang.logmein.http.ErrorResponse;
import com.egouletlang.logmein.model.Dealer;
import com.egouletlang.logmein.model.Deck;
import com.egouletlang.logmein.model.Game;
import com.egouletlang.logmein.model.score.Remaining;
import com.google.common.collect.Lists;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(value="Game Deck", tags = { "Game Deck" })
@SwaggerDefinition(tags = { @Tag(name = "Game Deck", description = "Change the state of the game deck") })
@RestController
@RequestMapping(path = "games/{id}/deck")
public class GameDeckController extends BaseController {

    public GameDeckController(GameRepository gameRepository, PlayerRepository playerRepository) {
        super(gameRepository, playerRepository);
    }

    // Game.Deck Resource API
    @ApiOperation(value = "View the current state of the game deck", response = Deck.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Current game deck. It can be empty"),
        @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetDeckRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game.getDeck(), HttpStatus.OK);
    }

    @ApiOperation(value = "Add a new deck to the game deck", response = Deck.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Current game deck"),
        @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostDeckAddRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Dealer.addDeck(game);
        this.gameRepository.save(game);

        return new ResponseEntity<>(game.getDeck(), HttpStatus.OK);
    }

    @ApiOperation(value = "Shuffle all cards remaining in the game deck", response = Deck.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Current game deck"),
        @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "/shuffle", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostDeckShuffleRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Dealer.shuffle(game);
        this.gameRepository.save(game);

        return new ResponseEntity<>(game.getDeck(), HttpStatus.OK);
    }

    @ApiOperation(value = "Collect all dealt cards from the players and returns them to the game deck", response = Deck.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Current game deck"),
        @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "/collect", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostDeckCollectRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Dealer.collect(game);
        this.gameRepository.save(game);

        return new ResponseEntity<>(game.getDeck(), HttpStatus.OK);
    }

    @ApiOperation(value = "Deal a number of cards to each play in the game, or to a specific player", response = Deck.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Current game deck"),
        @ApiResponse(code = 404, message = "No game with the provided game Id"),
        @ApiResponse(code = 406, message = "Insufficient cards left in the game deck")
    })
    @RequestMapping(path = "/deal", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostDeckDealRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id, @ApiParam(value = "Deal Information", required = true) final @RequestBody Optional<Map<String, Object>> input) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<String> players = Lists.newArrayList(game.getPlayers().keySet());
        int count = 1;

        try {
            Map<String, Object> body = input.orElse(null);

            if (body != null) {
                if (body.get("id") != null) {
                    String playerId = body.get("id").toString();
                    if (!game.getPlayers().keySet().contains(playerId)) {
                        throw new Exception(String.format("player '%s' is not part of this game", playerId));
                    }
                    players = Lists.newArrayList(body.get("id").toString());
                }

                if (body.get("count") != null) {
                    int requestedCount = ((Integer) body.get("count")).intValue();
                    if (requestedCount < 0) {
                        throw new Exception(String.format("'%d' is not a valid number of cards to deal", requestedCount));
                    }
                    count = requestedCount;
                }
            }
        } catch (Exception e) {
            return ErrorResponse.badRequest(e.getLocalizedMessage());
        }

        if (!Dealer.canDeal(game, players, count)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Dealer.deal(game, players, count);
        this.gameRepository.save(game);

        return new ResponseEntity<>(game.getDeck(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get a sorted list of the remaining cards count in the deck", response = Remaining.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "/remaining", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetDeckRemainingRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Remaining> remainingSuits = Dealer.countRemaining(game);
        Collections.sort(remainingSuits, Comparator.comparing(Remaining::getRaw).reversed());

        return new ResponseEntity<>(remainingSuits, HttpStatus.OK);
    }

    @ApiOperation(value = "Get a sorted list of remaining cards in the deck arranged by suit", response = Remaining.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "/remaining/suit", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetDeckRemainingSuitRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Remaining> remainingSuits = Dealer.countRemainingBySuits(game);
        Collections.sort(remainingSuits, Comparator.comparing(Remaining::getRaw).reversed());

        return new ResponseEntity<>(remainingSuits, HttpStatus.OK);
    }



}