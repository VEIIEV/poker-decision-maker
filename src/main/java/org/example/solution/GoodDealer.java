package org.example.solution;

import org.example.Board;
import org.example.Dealer;
import org.example.InvalidPokerBoardException;
import org.example.PokerResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoodDealer implements Dealer {

    private final LinkedList<String> cards;

    @SuppressWarnings("unchecked")
    private final static Function<List<String>, HandWeight>[] checks = new Function[]{
            (Function<List<String>, HandWeight>) GoodDealer::isRoyalFlush,
            (Function<List<String>, HandWeight>) GoodDealer::isStraightFlush,
            (Function<List<String>, HandWeight>) GoodDealer::isKare,
            (Function<List<String>, HandWeight>) GoodDealer::isFullHouse,
            (Function<List<String>, HandWeight>) GoodDealer::isFlush,
            (Function<List<String>, HandWeight>) GoodDealer::isStraight,
            (Function<List<String>, HandWeight>) GoodDealer::isSet,
            (Function<List<String>, HandWeight>) GoodDealer::isTwoPair,
            (Function<List<String>, HandWeight>) GoodDealer::isOnePair,
            (Function<List<String>, HandWeight>) GoodDealer::isHighCard,
    };


    public GoodDealer() {
        this.cards = new LinkedList<>();
        String[] suits = {"C", "D", "H", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(rank + suit);
            }
        }
        Collections.shuffle(cards);
    }

    @Override
    public Board dealCardsToPlayers() {
        StringBuilder player1 = new StringBuilder();
        StringBuilder player2 = new StringBuilder();

        for (int i = 0; i < 2; i++) {
            player1.append(this.cards.pop());
            player2.append(this.cards.pop());
        }

        return new Board(player1.toString(), player2.toString(), null, null, null);
    }

    @Override
    public Board dealFlop(Board board) {
        StringBuilder flop = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            flop.append(this.cards.pop());
        }

        return new Board(board.getPlayerOne(), board.getPlayerTwo(), flop.toString(), null, null);
    }

    @Override
    public Board dealTurn(Board board) {

        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), this.cards.pop(), null);
    }

    @Override
    public Board dealRiver(Board board) {

        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), board.getTurn(), this.cards.pop());
    }

    @Override
    public PokerResult decideWinner(Board board) throws InvalidPokerBoardException {

        List<String> cardsList = checkBoard(board);
        HandWeight firstPlayer = new HandWeight();
        HandWeight secondPlayer = new HandWeight();

        for (Function<List<String>, HandWeight> check : checks) {
            HandWeight result = check.apply(cardsList.subList(0, cardsList.size() - 2));
            if (result != null) {
                firstPlayer.setCombination(result.getCombination());
                firstPlayer.setWeight(result.getWeight());
                System.out.println("Found a valid combination!");
                break;
            }
        }
        for (Function<List<String>, HandWeight> check : checks) {
            HandWeight result = check.apply(cardsList.subList(2, cardsList.size()));
            if (result != null) {
                secondPlayer.setCombination(result.getCombination());
                secondPlayer.setWeight(result.getWeight());
                System.out.println("Found a valid combination!");
                break;
            }
        }
        //todo реализовать проверку кикеров с руки по незадействованным в комбинации картам в методе compareTo
        int result = firstPlayer.compareTo(secondPlayer);
        if (result > 0) return PokerResult.PLAYER_ONE_WIN;
        if (result < 0) return PokerResult.PLAYER_TWO_WIN;
        return PokerResult.DRAW;
    }

    private static HandWeight isRoyalFlush(List<String> cardsList) {
        List<String> ranks = List.of("10", "J", "Q", "K", "A");
        List<String> suits = List.of("C", "D", "H", "S");
        for (String suit : suits) {
            if (new HashSet<>(cardsList).containsAll(ranks.stream().map(rank -> rank + suit).collect(Collectors.toSet())))
                return new HandWeight(Combination.RoyalFlush, 1);
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

            for (int i = ranks.size() - 1; i >= 5; i--) {
                List<String> straight = ranks.subList(i - 5, i);
                if (new HashSet<>(suitedCards).containsAll(straight)) {
                    List<String> unusedCard = cardsList
                            .subList(0, 2).stream()
                            .filter(card -> !straight.contains(card)).toList();
                    return new HandWeight(Combination.StraightFlush, i, unusedCard);
                }
            }
        }

        return null;
    }

    private static HandWeight isKare(List<String> cardsList) {
        List<Integer> ranksList = new ArrayList<>(cardsList
                .stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(GoodDealer::parseRank)
                .toList());
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Integer card : ranksList) {
            rankCount.put(card, rankCount.getOrDefault(card, 0) + 1);
        }
        Integer rankOfKare = rankCount.entrySet().stream()
                .filter(entry -> entry.getValue() == 4)
                .map(Map.Entry::getKey)
                .findFirst().orElse(0);

        if (rankOfKare == 0) return null;
        List<Integer> hand = ranksList.subList(0, 2);
        hand.removeIf(rank -> rank == rankOfKare);
        return new HandWeight(Combination.Kare,
                rankOfKare,
                hand.stream().map(rank -> rank.toString() + "*").toList());

    }

    private static HandWeight isFullHouse(List<String> cardsList) {
        return null;
    }

    private static HandWeight isFlush(List<String> cardsList) {
        return null;
    }

    private static HandWeight isStraight(List<String> cardsList) {
        return null;
    }

    private static HandWeight isSet(List<String> cardsList) {
        return null;
    }

    private static HandWeight isTwoPair(List<String> cardsList) {
        return null;
    }

    private static HandWeight isOnePair(List<String> cardsList) {
        return null;
    }

    private static HandWeight isHighCard(List<String> cardsList) {
        return null;
    }


    /**
     * проверяет корректность карт на столе, возвращает список карт
     * 0-1 - рука игрока №1
     * 2-6 - общие карты
     * 7-8 - рука игрока №2
     */
    private List<String> checkBoard(Board board) throws InvalidPokerBoardException {

        List<String> cardsOnTable = new ArrayList<>();
        cardsOnTable.addAll(parseCards(board.getPlayerOne()));
        cardsOnTable.addAll(parseCards(board.getFlop()));
        cardsOnTable.add(board.getTurn());
        cardsOnTable.add(board.getRiver());
        cardsOnTable.addAll(parseCards(board.getPlayerTwo()));

        if (!(cardsOnTable.size() == new HashSet<>(cardsOnTable).size())) throw new InvalidPokerBoardException();
        return cardsOnTable;
    }

    private List<String> parseCards(String cardsInString) {
        List<String> cards = new ArrayList<>();
        int i = 0;

        while (i < cardsInString.length()) {
            int rankLength = (cardsInString.charAt(i) == '1') ? 2 : 1;
            String card = cardsInString.substring(i, i + rankLength + 1);
            cards.add(card);
            i += rankLength + 1;
        }

        return cards;
    }

    private static Integer parseRank(String rankInString) {
        return switch (rankInString) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            default -> Integer.parseInt(rankInString);
        };
    }
}
