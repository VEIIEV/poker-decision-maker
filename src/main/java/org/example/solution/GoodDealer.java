package org.example.solution;

import org.example.Board;
import org.example.Dealer;
import org.example.InvalidPokerBoardException;
import org.example.PokerResult;

import java.util.*;
import java.util.function.Function;

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
        int result = firstPlayer.compareTo(secondPlayer);
        if (result > 0) return PokerResult.PLAYER_ONE_WIN;
        if (result < 0) return PokerResult.PLAYER_TWO_WIN;
        return PokerResult.DRAW;
    }


    private static HandWeight isRoyalFlush(List<String> cardsList) {
        return null;
    }

    private static HandWeight isStraightFlush(List<String> cardsList) {
        return null;
    }

    private static HandWeight isKare(List<String> cardsList) {
        return null;
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
     *
     * @param board
     * @throws InvalidPokerBoardException
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
}
