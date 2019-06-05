package com.egouletlang.logmein.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@Document(collection = "player")
@ApiModel(description = "The Player State")
public class Player extends DbModel {

    @Indexed(unique = true)
    @ApiModelProperty(notes = "The player's username. Must be unique", required = true)
    private String username;

    @ApiModelProperty(notes = "The player's first name")
    private String firstName;

    @ApiModelProperty(notes = "The player's last name")
    private String lastName;

    @ApiModelProperty(notes = "A list of the games the player is currently playing")
    private Set<String> gameIds = new HashSet<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getGameIds() {
        return gameIds;
    }

    public void setGameIds(Set<String> gameIds) {
        this.gameIds = gameIds;
    }

    public void addGameId(String gameId) {
        this.gameIds.add(gameId);
    }

    public void removeGameId(String gameId) {
        this.gameIds.remove(gameId);
    }

}
