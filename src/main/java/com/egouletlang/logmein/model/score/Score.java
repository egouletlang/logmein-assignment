package com.egouletlang.logmein.model.score;

import com.egouletlang.logmein.model.Dealer;
import com.egouletlang.logmein.model.Player;

import java.util.List;

public class Score {

    private Player player;

    private List<Integer> cards;

    private Integer score;

    public Score(Player player, List<Integer> cards, Integer score) {
        this.player = player;
        this.cards = cards;
        this.score = score;
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getCards() {
        return Dealer.getAbbr(cards);
    }

    public Integer getScore() {
        return score;
    }
}
