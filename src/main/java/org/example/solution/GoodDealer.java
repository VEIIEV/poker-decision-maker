package org.example.solution;

import org.example.Board;
import org.example.Dealer;
import org.example.InvalidPokerBoardException;
import org.example.PokerResult;

import java.util.*;
import java.util.function.Function;

import static org.example.solution.Parser.parseBoard;
import static org.example.solution.Parser.parseCards;
import static org.example.solution.CombinationDefiner.getHandWeight;

public class GoodDealer implements Dealer {
    private final LinkedList<String> cards;

    private static final Map<Integer, Integer> STAGES = new HashMap<>();

    static {
        {
            STAGES.put(1, 4);
            STAGES.put(2, 7);
            STAGES.put(3, 8);
            STAGES.put(4, 9);
        }
    }

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
            player1.append(getCardAndRotate());
            player2.append(getCardAndRotate());
        }

        return new Board(player1.toString(), player2.toString(), null, null, null);
    }

    @Override
    public Board dealFlop(Board board) {
        if (board.getFlop() != null ||
                board.getTurn() != null ||
                board.getRiver() != null) {
            throw new InvalidPokerBoardException("Flop cards are already laid out on the board");
        }
        checkBoard(board, 1);

        StringBuilder flop = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            flop.append(getCardAndRotate());
        }
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), flop.toString(), null, null);
    }

    @Override
    public Board dealTurn(Board board) {
        if (board.getTurn() != null ||
                board.getRiver() != null) {
            throw new InvalidPokerBoardException("Turn cards are already laid out on the board");
        }
        checkBoard(board, 2);

        return new Board(board.getPlayerOne(),
                board.getPlayerTwo(),
                board.getFlop(),
                getCardAndRotate(), null);
    }

    @Override
    public Board dealRiver(Board board) {
        if (board.getRiver() != null) {
            throw new InvalidPokerBoardException("River cards are already laid out on the board");
        }
        checkBoard(board, 3);

        return new Board(board.getPlayerOne(),
                board.getPlayerTwo(),
                board.getFlop(),
                board.getTurn(),
                getCardAndRotate());
    }

    @Override
    public PokerResult decideWinner(Board board) throws InvalidPokerBoardException {
        List<String> cardsList = checkBoard(board, 4);
        HandWeight firstPlayer;
        HandWeight secondPlayer;

        List<String> player1Cards = cardsList.subList(0, cardsList.size() - 2);
        firstPlayer = getHandWeight(player1Cards);
        List<String> temp = cardsList.subList(2, cardsList.size());

        List<String> lastTwo = temp.subList(temp.size() - 2, temp.size());
        List<String> player2Cards = new ArrayList<>(lastTwo);
        player2Cards.addAll(temp.subList(0, temp.size() - 2));

        secondPlayer = getHandWeight(player2Cards);
        int result = firstPlayer.compareTo(secondPlayer);

        if (result > 0) return PokerResult.PLAYER_ONE_WIN;
        if (result < 0) return PokerResult.PLAYER_TWO_WIN;
        return PokerResult.DRAW;
    }




    /**
     * проверяет корректность карт на столе, возвращает список карт.
     * 0-1 - рука игрока №1
     * 2-6 - общие карты
     * 7-8 - рука игрока №2
     */
    private List<String> checkBoard(Board board, int stage) throws InvalidPokerBoardException {
        // todo мб переписать это на цикл, в котором через рефлексию получать все гетеры и выполнять их

        List<String> cardsOnTable = parseBoard(board, cards);
        if (STAGES.get(stage) != cardsOnTable.size()) {
            throw new InvalidPokerBoardException("the number of cards does not correspond to the stage of the game");
        }
        if (parseCards(board.getPlayerOne()).size() != 2) {
            throw new InvalidPokerBoardException("player one haven't 2 cards");
        }
        if (parseCards(board.getPlayerTwo()).size() != 2) {
            throw new InvalidPokerBoardException("player two haven't 2 cards");
        }

        if (stage > 1) {
            if (parseCards(board.getFlop()).size() != 3) {
                throw new InvalidPokerBoardException("flop haven't 3 cards");
            }
        }

        if (stage > 2) {
            if (parseCards(board.getTurn()).size() != 1) {
                throw new InvalidPokerBoardException("turn haven't 1 cards");
            }
        }

        if (stage > 3) {
            if (parseCards(board.getRiver()).size() != 1) {
                throw new InvalidPokerBoardException("river haven't 1 cards");
            }
        }

        if (!(cardsOnTable.size() == new HashSet<>(cardsOnTable).size())) {
            throw new InvalidPokerBoardException("cards on the board isnt unique");
        }

        return cardsOnTable;
    }

    private String getCardAndRotate() {
        String card = this.cards.pop();
        this.cards.addLast(card);
        return card;
    }
}
