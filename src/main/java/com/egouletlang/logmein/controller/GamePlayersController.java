package com.egouletlang.logmein.controller;

import com.egouletlang.logmein.db.GameRepository;
import com.egouletlang.logmein.db.PlayerRepository;
import com.egouletlang.logmein.http.ErrorResponse;
import com.egouletlang.logmein.model.Game;
import com.egouletlang.logmein.model.Hand;
import com.egouletlang.logmein.model.Player;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api(value="Game Players", tags = { "Game Players" })
@SwaggerDefinition(tags = { @Tag(name = "Game Players", description = "Add and remove players in a game") })
@RestController
@RequestMapping(path = "games/{game_id}/players")
public class GamePlayersController extends BaseController {

    public GamePlayersController(GameRepository gameRepository, PlayerRepository playerRepository) {
        super(gameRepository, playerRepository);
    }

    // Game.Players Resource API
    @ApiOperation(value = "View a list of player ids in a game", response = String.class, responseContainer="List")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved list. The list can be empty"),
        @ApiResponse(code = 404, message = "No game with the provided game Id")
    })
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetPlayersRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("game_id") String gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game.getPlayers().keySet(), HttpStatus.OK);
    }

    @ApiOperation(value = "Add an existing player to a game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The player was added to the game", response = String.class, responseContainer="List"),
            @ApiResponse(code = 400, message = "Issue parsing the player information provided"),
            @ApiResponse(code = 404, message = "Either Game with game Id not found or Player with player Id not found")
    })
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostPlayersRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("game_id") String gameId, @ApiParam(value = "Player Information", required = true) final @RequestBody Map<String, Object> input) {
        String playerId;
        try {
            this.validate(input, "id");
            playerId = input.get("id").toString();
        } catch (Exception e) {
            return ErrorResponse.badRequest(e.getLocalizedMessage());
        }

        Player player = playerRepository.findById(playerId).orElse(null);
        Game game = gameRepository.findById(gameId).orElse(null);

        if (player == null || game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            player.addGameId(gameId);
            playerRepository.save(player);

            game.addPlayer(playerId);
            gameRepository.save(game);

            return new ResponseEntity<>(game.getPlayers().keySet(), HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.internalServerError(e.getLocalizedMessage());
        }

    }

    @ApiOperation(value = "Remove a player from a game")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "The player was removed from the game"),
            @ApiResponse(code = 404, message = "Either Game with game Id not found or Player with player Id not found")
    })
    @RequestMapping(path = "/{player_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> handleDeletePlayersRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("game_id") String gameId, @ApiParam(value = "Player Id", required = true) @PathVariable("player_id") String playerId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        Game game = gameRepository.findById(gameId).orElse(null);

        if (player == null || game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            player.removeGameId(gameId);
            playerRepository.save(player);

            game.removePlayer(playerId);
            gameRepository.save(game);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return ErrorResponse.internalServerError(e.getLocalizedMessage());
        }

    }

    @ApiOperation(value = "Get player's hand", response = Hand.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list. The list can be empty"),
            @ApiResponse(code = 404, message = "Either Game with game Id not found or Player with player Id not found")
    })
    @RequestMapping(path = "/{player_id}/hand", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetPlayerHandRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("game_id") String gameId, @ApiParam(value = "Player Id", required = true) @PathVariable("player_id") String playerId) {

        Game game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Integer> hand = game.getPlayers().get(playerId);
        if (hand == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Hand(hand), HttpStatus.OK);

    }

}