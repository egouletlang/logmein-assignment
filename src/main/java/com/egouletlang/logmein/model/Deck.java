package com.egouletlang.logmein.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import jdk.jfr.Unsigned;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A deck is a non-repeating set of 52 cards spanning 13 denominations [A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K] and
 * four suits [clubs, diamonds, hearts, spades]
 *
 * We can take advantage of the internal hex representation to encode the identify of each card
 *
 * Byte 0 -> Denomination
 *  0 -> Ace
 *  1 -> Two
 *  2 -> Three
 *  3 -> Four
 *  4 -> Five
 *  5 -> Six
 *  6 -> Seven
 *  7 -> Eight
 *  8 -> Nine
 *  9 -> Ten
 *  A -> Jack
 *  B -> Queen
 *  C -> King
 *  D - F -> Invalid
 *
 * Byte 1 -> Suit and given the problem assignement order (hearts, spades, clubs, and diamonds)
 *  0 -> Diamonds
 *  1 -> Clubs
 *  2 -> Spades
 *  3 -> Hearts
 *
 *
 *
 *  e.g. the J of diamonds is 0x1A
 *
 *  Simple enough
 *
 * */

//@Document
public class Deck {

    private List<Integer> cards = new ArrayList<>();

    public List<Integer> getCards() {
        return cards;
    }

    public Integer size() {
        return cards.size();
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public void addCards(@NonNull List<Integer> cards) {
        this.cards.addAll(cards);
    }

    public void removeCards() {
        if (this.cards != null) {
            cards.clear();
        }
    }

    public Integer remove(int index) {
        int count = this.cards.size();
        if (index < 0 || index >= count) {
            return null;
        }

        return cards.remove(index);
    }

    public void swap(int index1, int index2) {
        int count = this.cards.size();
        if (index1 < 0 || index2 < 0 || index1 >= count || index2 >= count) {
            return;
        }

        Integer temp = this.cards.get(index1);
        this.cards.set(index1, this.cards.get(index2));
        this.cards.set(index2, temp);
    }

}
