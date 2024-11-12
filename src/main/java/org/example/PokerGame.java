package org.example;

import org.example.solution.GoodDealer;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = dealer.dealCardsToPlayers();
        board = dealer.dealFlop(board);
        board = dealer.dealTurn(board);
        board = dealer.dealRiver(board);
        dealer.decideWinner(board);

//        System.out.println(board.toString());
    }
}
