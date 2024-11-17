package org.example;

import org.example.solution.GoodDealer;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = dealer.dealCardsToPlayers();
        board = dealer.dealFlop(board);
        board = dealer.dealTurn(board);
        dealer.dealFlop(new Board("AD4D7D", "6D", null, null, null));
        board = dealer.dealRiver(board);
        System.out.println(board.toString());
        System.out.println(dealer.decideWinner(board));

    }
}



