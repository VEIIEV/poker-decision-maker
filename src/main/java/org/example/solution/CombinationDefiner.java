package org.example.solution;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.solution.GoodDealer.searchMaxRankOfStackedCards;
import static org.example.solution.Parser.parseRank;

public class CombinationDefiner {
    protected static HandWeight isRoyalFlush(List<String> cardsList) {
        List<String> ranks = List.of("10", "J", "Q", "K", "A");
        List<String> suits = List.of("C", "D", "H", "S");
        for (String suit : suits) {
            if (new HashSet<>(cardsList)
                    .containsAll(ranks
                            .stream()
                            .map(rank -> rank + suit)
                            .collect(Collectors.toSet()))) {
                return new HandWeight(Combination.RoyalFlush, 1);
            }
        }

        return null;
    }

    protected static HandWeight isStraightFlush(List<String> cardsList) {
        List<String> ranks = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
        List<String> suits = List.of("C", "D", "H", "S");

        for (String suit : suits) {
            List<String> suitedCards = cardsList.stream()
                    .filter(card -> card.endsWith(suit))
                    .map(card -> card.substring(0, card.length() - 1))
                    .toList();

            for (int i = ranks.size() - 1; i >= 5; i--) {
                List<String> straight = ranks.subList(i - 5, i);
                if (new HashSet<>(suitedCards).containsAll(straight)) {
                    List<String> unused = cardsList.subList(0, 2);
                    unused.removeIf(suitedCards::contains);
                    return new HandWeight(Combination.StraightFlush, i, unused);
                }
            }
        }

        return null;
    }

    protected static HandWeight isKare(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 4, Combination.Kare);
    }

    protected static HandWeight isFullHouse(List<String> cardsList) {
        HandWeight setPart = searchMaxRankOfStackedCards(cardsList, 3, Combination.Set);
        if (setPart == null) return null;
        int setWeight = setPart.getWeight() * 100;
        List<String> remainsCards = cardsList
                .stream()
                .map(card -> parseRank(card.substring(0, card.length() - 1)) == setPart.getWeight() ? "0*" : card)
                .toList();
        HandWeight pairPart = searchMaxRankOfStackedCards(remainsCards, 2, Combination.OnePair);
        if (pairPart == null) return null;
        return new HandWeight(Combination.FullHouse,
                setWeight + pairPart.getWeight(),
                pairPart.getUnusedCard());
    }

    protected static HandWeight isFlush(List<String> cardsList) {
        List<String> suits = List.of("C", "D", "H", "S");

        for (String suit : suits) {
            List<String> suitedCards = cardsList.stream()
                    .filter(card -> card.endsWith(suit))
                    .toList();
            if (suitedCards.size() < 5) continue;
            Integer weight = suitedCards
                    .stream()
                    .map(card -> card.substring(0, card.length() - 1))
                    .map(Parser::parseRank)
                    .max(Integer::compareTo).get();
            List<String> unused = cardsList.subList(0, 2);
            unused.removeIf(suitedCards::contains);
            return new HandWeight(Combination.Flush, weight, unused);
        }
        return null;
    }

    protected static HandWeight isStraight(List<String> cardsList) {
        List<String> ranks = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

        for (int i = ranks.size() - 1; i >= 5; i--) {
            List<String> straight = ranks.subList(i - 5, i);
            if (new HashSet<>(cardsList).containsAll(straight)) {
                List<String> unused = cardsList.subList(0, 2);
                unused.removeIf(straight::contains);
                return new HandWeight(Combination.Straight, i, unused);
            }
        }

        return null;
    }

    protected static HandWeight isSet(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 3, Combination.Set);
    }

    protected static HandWeight isTwoPair(List<String> cardsList) {
        HandWeight firstPart = searchMaxRankOfStackedCards(cardsList, 2, Combination.OnePair);
        if (firstPart == null) return null;
        int firstPairWeight = firstPart.getWeight() * 100;
        List<String> remainsCards = cardsList
                .stream()
                .map(card -> parseRank(card.substring(0, card.length() - 1)) == firstPart.getWeight() ? "0*" : card)
                .toList();
        HandWeight secondPair = searchMaxRankOfStackedCards(remainsCards, 2, Combination.OnePair);
        if (secondPair == null) return null;
        return new HandWeight(Combination.TwoPair,
                firstPairWeight + secondPair.getWeight(),
                secondPair.getUnusedCard());
    }

    protected static HandWeight isOnePair(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 2, Combination.OnePair);
    }

    protected static HandWeight isHighCard(List<String> cardsList) {
        Integer weight = cardsList.subList(0, 2).stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(Parser::parseRank)
                .max(Integer::compareTo).orElse(0);

        return new HandWeight(Combination.HighCard, weight, cardsList.subList(0, 2));
    }
}
