package org.example.solution;

import org.example.Board;
import org.example.InvalidPokerBoardException;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    protected static List<String> parseBoard(Board board, List<String> cards) {
        List<String> cardsOnTable = new ArrayList<>();
        cardsOnTable.addAll(parseCards(board.getPlayerOne()));
        cardsOnTable.addAll(parseCards(board.getFlop()));
        cardsOnTable.addAll(parseCards(board.getTurn()));
        cardsOnTable.addAll(parseCards(board.getRiver()));
        cardsOnTable.addAll(parseCards(board.getPlayerTwo()));
        for (String card : cardsOnTable) {
            if (!cards.contains(card)) {
                throw new InvalidPokerBoardException("Invalid card found on the table: " + card);
            }
        }
        return cardsOnTable;
    }

    protected static List<String> parseCards(String cardsInString) {
        if (cardsInString == null) return new ArrayList<>();
        List<String> cards = new ArrayList<>();
        int i = 0;
        if (cardsInString.length() < 2) {
            throw new InvalidPokerBoardException("cards on table contains inappropriate symbols");
        }
        while (i < cardsInString.length() - 1) {
            int rankLength = (cardsInString.charAt(i) == '1') ? 2 : 1;
            String card = cardsInString.substring(i, i + rankLength + 1);

            cards.add(card);
            i += rankLength + 1;
        }

        return cards;
    }

    protected static Integer parseRank(String rankInString) {
        return switch (rankInString) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            default -> Integer.parseInt(rankInString);
        };
    }


}
