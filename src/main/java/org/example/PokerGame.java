package org.example;

import org.example.solution.GoodDealer;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = dealer.dealCardsToPlayers();
        board = dealer.dealFlop(board);
        board = dealer.dealTurn(board);
        board = dealer.dealRiver(board);

        System.out.println(board);
        board = new Board("2D3S", "3D3H", "QSJSKS", "10S", "AS");
        System.out.println(dealer.decideWinner(board));
    }
}



