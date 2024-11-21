package org.example;

import org.example.solution.GoodDealer;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = dealer.dealCardsToPlayers();
        board = dealer.dealFlop(board);
        board = dealer.dealTurn(board);
        board = dealer.dealRiver(board);

        board = new Board(
                "KDKS",
                "3D5H",     // Игрок 2: карты одной масти, часть стрита
                "KH3H6H",        // Общие карты для формирования флеша и стрита
                "2H",           // Продолжение стрита
                "4H"            // Завершение стрита
        );
        System.out.println(board);
        System.out.println(dealer.decideWinner(board));
    }
}



