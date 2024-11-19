package org.example;

import org.example.solution.GoodDealer;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = new Board( "2CJH",  "2DJD",  "3H9SKH",  "QC",  "AS");

        PokerResult result = dealer.decideWinner(board);

        System.out.println(result);

    }
}



