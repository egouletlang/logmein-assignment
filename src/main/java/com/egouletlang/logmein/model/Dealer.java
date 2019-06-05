package com.egouletlang.logmein.model;

import com.egouletlang.logmein.model.score.Remaining;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Dealer {

    private final static Map<Integer, String> suits = createSuitMap();

    private final static Map<Integer, String> denominations = createDenominationMap();

    private static Map<Integer, String> createSuitMap() {
        Map<Integer, String> suitMap = new HashMap<>();
        suitMap.put(0x0, "Diamonds");
        suitMap.put(0x1, "Clubs");
        suitMap.put(0x2, "Spades");
        suitMap.put(0x3, "Hearts");
        return suitMap;
    }

    private static Map<Integer, String> createDenominationMap() {
        Map<Integer, String> denominationMap = new HashMap<>();
        denominationMap.put(0x0, "Ace");
        denominationMap.put(0x1, "Two");
        denominationMap.put(0x2, "Three");
        denominationMap.put(0x3, "Four");
        denominationMap.put(0x4, "Five");
        denominationMap.put(0x5, "Six");
        denominationMap.put(0x6, "Seven");
        denominationMap.put(0x7, "Eight");
        denominationMap.put(0x8, "Nine");
        denominationMap.put(0x9, "Ten");
        denominationMap.put(0xA, "Jack");
        denominationMap.put(0xB, "Queen");
        denominationMap.put(0xC, "King");
        return denominationMap;
    }

    public static Deck createNewDeck() {
        Deck deck = new Deck();
        List<Integer> cards = new ArrayList<>();

        for (Integer suitKey: suits.keySet()) {
            for (Integer denominatorKey: denominations.keySet()) {
                cards.add(getEncodedValue(suitKey, denominatorKey));
            }
        }

        deck.setCards(cards);
        return deck;
    }


    private static Integer getEncodedValue(@NonNull Integer suit, @NonNull Integer denomination) {
        return (suit << 4) + denomination;
    }

    private static Integer getEffectiveValue(@NonNull Integer card) {
        Integer denominatorKey = getDenominatorKey(card);
        return (denominatorKey == null) ? 0 : denominatorKey + 1;
    }

    private static Integer getSuitKey(@NonNull Integer cardValue) {
        return (cardValue & 0xF0) >> 4;
    }

    private static Integer getDenominatorKey(@NonNull Integer cardValue) {
        return cardValue & 0x0F;
    }

    private static String getSuit(@NonNull Integer card) {
        return suits.get(getSuitKey(card));
    }

    private static String getSuitAbbr(@NonNull Integer card) {
        Integer suitKey = getSuitKey(card);
        return getSuitAbbrFromSuitKey(suitKey);
    }

    private static String getSuitAbbrFromSuitKey(@NonNull Integer suitKey) {
        if (suitKey == 0) {
            return "♢";
        } else if (suitKey == 1) {
            return "♣";
        } else if (suitKey == 2) {
            return "♠";
        } else if (suitKey == 3) {
            return "♡";
        }

        return null;
    }

    private static String getDenomination(@NonNull Integer card) {
        return denominations.get(getDenominatorKey(card));
    }

    private static String getDenominationAbbr(@NonNull Integer card) {
        Integer denominatorKey = getDenominatorKey(card);

        if (denominatorKey == 0) {
            return "A";
        } else if (denominatorKey > 0 && denominatorKey <= 0x9) {
            return String.format("%d", denominatorKey + 1);
        } else if (denominatorKey == 0xA) {
            return "J";
        } else if (denominatorKey == 0xB) {
            return "Q";
        } else if (denominatorKey == 0xC) {
            return "K";
        }
        return null;
    }




    // Card Interface
    public static String getName(Integer card) {
        String suit = getSuit(card);
        String denomination = getDenomination(card);

        if (suit == null || denomination == null) {
            return "Unknown";
        }

        return String.format("%s of %s", denomination, suit);
    }

    public static String getAbbr(Integer card) {
        String suit = getSuitAbbr(card);
        String denomination = getDenominationAbbr(card);

        if (suit == null || denomination == null) {
            return "UN";
        }

        return String.format("%s%s", denomination, suit);
    }

    public static Integer getTotal(List<Integer> cards) {
        return cards.stream()
                    .map(card -> getEffectiveValue(card))
                    .reduce(0, Integer::sum);
    }

    public static List<String> getNames(List<Integer> cards) {
        return cards.stream()
                .map(card -> getName(card))
                .collect(Collectors.toList());
    }

    public static List<String> getAbbr(List<Integer> cards) {
        return cards.stream()
                .map(card -> getAbbr(card))
                .collect(Collectors.toList());
    }

    // Deck Interface

    public static List<String> getNames(Deck deck) {
        return getNames(deck.getCards());
    }

    public static List<String> getAbbr(Deck deck) {
        return getAbbr(deck.getCards());
    }

    public static void addDeck(Game game) {
        if (game == null) {
            return;
        }
        game.addDeck(Dealer.createNewDeck());
    }

    public static void shuffle(Game game) {
        if (game == null) {
            return;
        }

        Deck deck = game.getDeck();
        if (deck == null) {
            return;
        }

        Random r = new Random();

        int count = deck.getCards().size();
        for (int i = 0; i < count; i++) {
            int swapIndex = r.nextInt(count - i) + i;
            deck.swap(i, swapIndex);
        }

    }

    public static void collect(Game game) {
        if (game == null) {
            return;
        }

        Integer deckCount = game.getDeckCount();

        // Correct the deck
        Deck deck = game.getDeck();
        if (deck != null) {
            deck.removeCards();
        }

        for (int i = 0; i < deckCount; i++) {
            game.addDeck(createNewDeck());
        }

        // Correct the deck count
        game.setDeckCount(deckCount);

        // TODO: modify the players
        game.clearHands();

    }

    public static boolean canDeal(Game game, List<String> players, int count) {
        if (game == null || players == null) {
            return false;
        }

        Deck deck = game.getDeck();

        if (deck == null) {
            return false;
        }

        int totalCards = count * players.size();


        return totalCards < deck.size();
    }

    public static void deal(Game game, List<String> players, int count) {
        if (!canDeal(game, players, count)) {
            return;
        }

        //canDeal does the null checks

        Deck deck = game.getDeck();

        for (int i = 0; i < count; i++) {
            for (String player: players) {
                Integer card = deck.remove(0);
                game.addCard(player, card);
            }
        }


    }

    public static List<Remaining> countRemainingBySuits(Game game) {
        if (game == null) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> ret = new HashMap<>();

        for (Integer card: game.getDeck().getCards()) {
            Integer suit = getSuitKey(card);
            ret.merge(suit, 1, Integer::sum);
        }

        return ret.entrySet().stream()
                             .map(e -> new Remaining(e.getKey(), getSuitAbbrFromSuitKey(e.getKey()), e.getValue()))
                             .collect(Collectors.toList());

    }

    public static List<Remaining> countRemaining(Game game) {
        if (game == null) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> ret = new HashMap<>();

        for (Integer card: game.getDeck().getCards()) {
            ret.merge(card, 1, Integer::sum);
        }

        return ret.entrySet().stream()
                .map(e -> new Remaining(e.getKey(), getAbbr(e.getKey()), e.getValue()))
                .collect(Collectors.toList());
    }

}
