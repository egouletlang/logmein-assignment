package com.egouletlang.logmein.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Document(collection = "game")
@ApiModel(description = "The Game State")
public class Game extends DbModel {

	@Indexed(unique = true)
    @ApiModelProperty(notes = "The game name. Must be unique", required = true)
    private String name;

    @ApiModelProperty(notes = "The number of decks currently in the game", readOnly = true)
	private Integer deckCount = 0;

    @ApiModelProperty(notes = "The game deck", readOnly = true)
	private Deck deck = new Deck();

    @ApiModelProperty(notes = "A list of the players and the cards in their hand", readOnly = true)
	private Map<String, List<Integer>> players = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDeckCount() {
        return deckCount;
    }

    public void setDeckCount(Integer deckCount) {
        this.deckCount = deckCount;
    }

    public Deck getDeck() {
        return deck;
    }

    public void addDeck(Deck deck) {
        deckCount += 1;
        this.deck.addCards(deck.getCards());
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Map<String, List<Integer>> getPlayers() {
        return players;
    }

    public boolean hasPlayer(String playerId) {
        return this.players.containsKey(playerId);
    }

    public void addPlayer(String playerId) {
        this.players.put(playerId, new ArrayList<>());
    }

    public void removePlayer(String playerId) {
        this.players.remove(playerId);
    }

    public void setPlayers(Map<String, List<Integer>> players) {
        this.players = players;
    }

    public void addCard(String playerId, Integer card) {
        this.players.get(playerId).add(card);
    }

    public void clearHands() {
        for (String playerId: this.players.keySet()) {
            this.players.get(playerId).clear();
        }
    }

}