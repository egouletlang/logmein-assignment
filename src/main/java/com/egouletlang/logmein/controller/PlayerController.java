package com.egouletlang.logmein.controller;

import com.egouletlang.logmein.db.GameRepository;
import com.egouletlang.logmein.db.PlayerRepository;
import com.egouletlang.logmein.http.ErrorResponse;
import com.egouletlang.logmein.http.request.PlayerInformation;
import com.egouletlang.logmein.model.Game;
import com.egouletlang.logmein.model.Player;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Api(value="Players", tags = { "Players" }, position = 1)
@SwaggerDefinition(tags = { @Tag(name = "Players", description = "Create, find and delete players") })
@RestController
@RequestMapping(path = "players")
public class PlayerController extends BaseController {

    public PlayerController(GameRepository gameRepository, PlayerRepository playerRepository) {
        super(gameRepository, playerRepository);
    }

    // Game Resource API
    @ApiOperation(value = "View a list of players", response = Player.class, responseContainer="List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list. The list can be empty") })
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetPlayersRequest(@ApiParam(value = "Player Username") @RequestParam("username") Optional<String> username) {
        if (username.isPresent()) {
            Player player = this.playerRepository.findByUsername(username.get());
            if (player != null) {
                return new ResponseEntity<>(player, HttpStatus.OK);
            }
        } else {
            List<Player> players = playerRepository.findAll();
            if (players != null) {
                return new ResponseEntity<>(players, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(JsonNodeFactory.instance.arrayNode(), HttpStatus.OK);
    }

    @ApiOperation(value = "Create a new player")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The player was created successfully", response = Game.class),
            @ApiResponse(code = 400, message = "Issue parsing the player information provided"),
            @ApiResponse(code = 409, message = "The player username provided is not unique")
    })
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<?> handlePostPlayersRequest(@ApiParam(value = "Player Information", required = true) final @RequestBody PlayerInformation playerInformation) {
        String username = playerInformation.getUsername();
        String firstName = playerInformation.getFirstName();
        String lastName = playerInformation.getLastName();

        if (username == null) {
            return ErrorResponse.badRequest(String.format("You must specify a value for 'username'"));
        }

        // check if the name already exists
        if (this.playerRepository.findByUsername(username) != null) {
            return ErrorResponse.conflict(String.format("A player with the username '%s' already exists", username));
        }

        try {
            final Player player = new Player();
            player.generateId();
            player.setUsername(username);
            player.setFirstName(firstName);
            player.setLastName(lastName);
            this.playerRepository.save(player);
            return new ResponseEntity<>(player, HttpStatus.CREATED);
        } catch (Exception e) {
            return ErrorResponse.internalServerError(e.getLocalizedMessage());
        }

    }

    @ApiOperation(value = "Lookup player by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the player", response = Game.class),
            @ApiResponse(code = 404, message = "No player with the provided Player Id")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetPlayerByIdRequest(@ApiParam(value = "Player Id", required = true) @PathVariable("id") String id) {
        Player player = playerRepository.findById(id).orElse(null);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete player by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully delete the player"),
            @ApiResponse(code = 404, message = "No game with the provided player Id")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> handleDeletePlayerByIdRequest(@ApiParam(value = "Player Id", required = true) @PathVariable("id") String id) {
        Player player = playerRepository.findById(id).orElse(null);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Set<String> gameIds = player.getGameIds();
        List<Game> games = gameIds.stream()
                .map(gameId -> gameRepository.findById(gameId))
                .filter(input -> input.isPresent())
                .map(gameOptional -> gameOptional.get())
                .collect(Collectors.toList());

        for (Game game: games) {
            game.removePlayer(id);
        }

        if (games.size() > 0) {
            gameRepository.saveAll(games);
        }

        try {
            this.playerRepository.delete(player);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return ErrorResponse.internalServerError(e.getLocalizedMessage());
        }
    }

}