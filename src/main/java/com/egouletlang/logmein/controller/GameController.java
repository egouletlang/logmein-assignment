package com.egouletlang.logmein.controller;

import com.egouletlang.logmein.db.GameRepository;
import com.egouletlang.logmein.db.PlayerRepository;
import com.egouletlang.logmein.http.ErrorResponse;
import com.egouletlang.logmein.http.request.GameInformation;
import com.egouletlang.logmein.model.Game;
import com.egouletlang.logmein.model.Player;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.Lists;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Api(value="Games", tags = { "Games" })
@SwaggerDefinition(tags = { @Tag(name = "Games", description = "Create, find and delete game instances") })
@RestController
@RequestMapping(path = "games")
public class GameController extends BaseController {

    public GameController(GameRepository gameRepository, PlayerRepository playerRepository) {
        super(gameRepository, playerRepository);
    }

    // Game Resource API
    @ApiOperation(value = "View a list of games", response = Game.class, responseContainer="List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list. The list can be empty") })
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetGamesRequest(@ApiParam(value = "Game Name") @RequestParam("name") Optional<String> name) {
        if (name.isPresent()) {
            Game game = this.gameRepository.findByName(name.get());
            if (game != null) {
                return new ResponseEntity<>(Lists.newArrayList(game), HttpStatus.OK);
            }
        } else {
            List<Game> games = gameRepository.findAll();
            if (games != null) {
                return new ResponseEntity<>(games, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(JsonNodeFactory.instance.arrayNode(), HttpStatus.OK);
    }

    @ApiOperation(value = "Create a new game", response = Game.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The game was created successfully", response = Game.class),
            @ApiResponse(code = 400, message = "Issue parsing the game information provided"),
            @ApiResponse(code = 409, message = "The game name provided is not unique")
    })
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostGamesRequest(@ApiParam(value = "Game Information", required = true, example = "{'name': 'game1'}") final @RequestBody GameInformation info) {
        String name = info.getName();
        if (name == null) {
            return ErrorResponse.badRequest(String.format("You must specify a value for 'name'"));
        }

        // check if the name already exists
        if (this.gameRepository.findByName(name) != null) {
            return ErrorResponse.conflict(String.format("A game with the name '%s' already exists", name));
        }

        try {
            final Game game = new Game();
            game.generateId();
            game.setName(name);
            this.gameRepository.save(game);
            return new ResponseEntity<>(game, HttpStatus.CREATED);
        } catch (Exception e) {
            return ErrorResponse.internalServerError(e.getLocalizedMessage());
        }

    }

    @ApiOperation(value = "Lookup game by ID", response = Game.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the game", response = Game.class),
            @ApiResponse(code = 404, message = "No game with the provided Game Id")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetGameByIdRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete game by ID", response = Game.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully delete the game"),
            @ApiResponse(code = 404, message = "No game with the provided Game Id")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> handleDeleteGameByIdRequest(@ApiParam(value = "Game Id", required = true) @PathVariable("id") String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Set<String> playerIds = game.getPlayers().keySet();

        List<Player> players = playerIds.stream()
                .map(gameId -> playerRepository.findById(gameId))
                .filter(input -> input.isPresent())
                .map(gameOptional -> gameOptional.get())
                .collect(Collectors.toList());


        for (Player player: players) {
            player.removeGameId(id);
        }

        if (players.size() > 0) {
            playerRepository.saveAll(players);
        }

        try {
            this.gameRepository.delete(game);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return ErrorResponse.internalServerError(e.getLocalizedMessage());
        }

    }

}