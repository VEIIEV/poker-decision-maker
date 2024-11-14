package org.example;

import org.example.solution.GoodDealer;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = dealer.dealCardsToPlayers();
        board = dealer.dealFlop(board);
        board = dealer.dealTurn(board);
        board = dealer.dealRiver(board);
//        board = dealer.dealFlop(board);
        System.out.println(board.toString());
        System.out.println(dealer.decideWinner(board));

    }
}


// я проверяю стадию, соответствует ли количество карт на столе текущей стадии

