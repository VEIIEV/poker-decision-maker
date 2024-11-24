package org.example.solution;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.solution.Parser.parseRank;

public class CombinationDefiner {
    @SuppressWarnings("unchecked")
    private static final Function<List<String>, HandWeight>[] CHECKS = new Function[]{
            (Function<List<String>, HandWeight>) CombinationDefiner::isRoyalFlush,
            (Function<List<String>, HandWeight>) CombinationDefiner::isStraightFlush,
            (Function<List<String>, HandWeight>) CombinationDefiner::isKare,
            (Function<List<String>, HandWeight>) CombinationDefiner::isFullHouse,
            (Function<List<String>, HandWeight>) CombinationDefiner::isFlush,
            (Function<List<String>, HandWeight>) CombinationDefiner::isStraight,
            (Function<List<String>, HandWeight>) CombinationDefiner::isSet,
            (Function<List<String>, HandWeight>) CombinationDefiner::isTwoPair,
            (Function<List<String>, HandWeight>) CombinationDefiner::isOnePair,
            (Function<List<String>, HandWeight>) CombinationDefiner::isHighCard,
    };

    protected static HandWeight getHandWeight(List<String> player1Cards) {
        HandWeight hand = new HandWeight();
        for (Function<List<String>, HandWeight> check : CHECKS) {
            HandWeight result = check.apply(player1Cards);
            if (result != null) {
                hand = result;
                break;
            }
        }
        return hand;
    }

    private static HandWeight isRoyalFlush(List<String> cardsList) {
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

    private static HandWeight isStraightFlush(List<String> cardsList) {
        List<String> ranks = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
        List<String> suits = List.of("C", "D", "H", "S");

        for (String suit : suits) {
            List<String> suitedCards = cardsList.stream()
                    .filter(card -> card.endsWith(suit))
                    .map(card -> card.substring(0, card.length() - 1))
                    .toList();
            if (suitedCards.size() > 4) {
                for (int i = ranks.size() - 1; i >= 5; i--) {
                    List<String> straight = ranks.subList(i - 5, i);
                    if (new HashSet<>(suitedCards).containsAll(straight)) {
                        List<String> unused = cardsList.subList(0, 2);
                        unused.removeIf(suitedCards::contains);
                        return new HandWeight(Combination.StraightFlush, i + 1, unused);
                    }
                }
            }
        }

        return null;
    }

    private static HandWeight isKare(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 4, Combination.Kare);
    }

    private static HandWeight isFullHouse(List<String> cardsList) {
        HandWeight setPart = searchMaxRankOfStackedCards(cardsList, 3, Combination.Set);
        if (setPart == null) {
            return null;
        }
        int setWeight = setPart.getWeight() * 100;
        List<String> remainsCards = cardsList
                .stream()
                .map(card -> parseRank(card.substring(0, card.length() - 1)) == setPart.getWeight() ? "0*" : card)
                .toList();
        HandWeight pairPart = searchMaxRankOfStackedCards(remainsCards, 2, Combination.OnePair);
        if (pairPart == null) {
            return null;
        }
        return new HandWeight(Combination.FullHouse,
                setWeight + pairPart.getWeight(),
                pairPart.getUnusedCard());
    }

    private static HandWeight isFlush(List<String> cardsList) {
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

    private static HandWeight isStraight(List<String> cardsList) {
        List<String> ranks = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

        for (int i = ranks.size() - 1; i >= 5; i--) {
            List<String> straight = ranks.subList(i - 5, i);
            if (new HashSet<>(cardsList.stream().map(card -> card.substring(0, 1)).toList()).containsAll(straight)) {
                List<String> unused = cardsList.subList(0, 2);
                unused.removeIf(straight::contains);
                return new HandWeight(Combination.Straight, i + 1, unused);
            }
        }

        return null;
    }

    private static HandWeight isSet(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 3, Combination.Set);
    }

    private static HandWeight isTwoPair(List<String> cardsList) {
        HandWeight firstPart = searchMaxRankOfStackedCards(cardsList, 2, Combination.OnePair);
        if (firstPart == null) {
            return null;
        }
        int firstPairWeight = firstPart.getWeight() * 100;
        List<String> remainsCards = cardsList
                .stream()
                .map(card -> parseRank(card.substring(0, card.length() - 1)) == firstPart.getWeight() ? "0*" : card)
                .toList();
        HandWeight secondPair = searchMaxRankOfStackedCards(remainsCards, 2, Combination.OnePair);
        if (secondPair == null) {
            return null;
        }
        return new HandWeight(Combination.TwoPair,
                firstPairWeight + secondPair.getWeight(),
                secondPair.getUnusedCard());
    }

    private static HandWeight isOnePair(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 2, Combination.OnePair);
    }

    private static HandWeight isHighCard(List<String> cardsList) {
        Integer weight = cardsList.subList(0, 2).stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(Parser::parseRank)
                .max(Integer::compareTo).orElse(0);

        return new HandWeight(Combination.HighCard, weight, cardsList.subList(0, 2));
    }

    private static HandWeight searchMaxRankOfStackedCards(List<String> cardsList, int amount, Combination combination) {
        List<Integer> ranksList = new ArrayList<>(cardsList
                .stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(Parser::parseRank)
                .toList());
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Integer card : ranksList) {
            rankCount.put(card, rankCount.getOrDefault(card, 0) + 1);
        }
        Integer maxRank = rankCount.entrySet().stream()
                .filter(entry -> entry.getValue() == amount)
                .map(Map.Entry::getKey)
                .max(Integer::compare).orElse(0);

        if (maxRank == 0) {
            return null;
        }
        ranksList.removeIf(rank -> Objects.equals(rank, maxRank));
        return new HandWeight(combination,
                maxRank,
                ranksList.stream().map(rank -> rank.toString() + "*").toList());
    }
}
