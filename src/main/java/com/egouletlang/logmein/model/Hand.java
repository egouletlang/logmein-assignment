package com.egouletlang.logmein.model;

import java.util.Arrays;
import java.util.List;

public class Hand {

    private List<Integer> cards;

    public Hand(List<Integer> cards) {
        this.cards = cards;
    }

    public List<String> getNames() {
        return Dealer.getNames(cards);
    }

    public List<String> getAbbr() {
        return Dealer.getAbbr(cards);
    }

    public Integer getScore() {
        return Dealer.getTotal(cards);
    }
}
