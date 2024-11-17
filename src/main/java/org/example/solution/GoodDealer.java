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

    private static final Map<Integer, Integer> stages = new HashMap<>();

    static {
        {
            stages.put(1, 4);
            stages.put(2, 7);
            stages.put(3, 8);
            stages.put(4, 9);
        }
    }


    public GoodDealer() {
//todo возможно стоит изначально хранить карты в set
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
        Board actualBoard = new Board(board.getPlayerOne(), board.getPlayerTwo(), flop.toString(), null, null);
        return actualBoard;
    }

    @Override
    public Board dealTurn(Board board) {
        if (board.getTurn() != null ||
                board.getRiver() != null) {
            throw new InvalidPokerBoardException("Flop cards are already laid out on the board");
        }
        checkBoard(board, 2);

        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), getCardAndRotate(), null);

    }

    @Override
    public Board dealRiver(Board board) {
        if (board.getRiver() != null) {
            throw new InvalidPokerBoardException("Flop cards are already laid out on the board");
        }
        checkBoard(board, 3);

        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), board.getTurn(), getCardAndRotate());
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

    private HandWeight getHandWeight(List<String> player1Cards) {
        HandWeight hand = new HandWeight();
        for (Function<List<String>, HandWeight> check : checks) {
            HandWeight result = check.apply(player1Cards);
            if (result != null) {
                hand = result;
                break;
            }
        }
        return hand;
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
                    List<String> unused = cardsList.subList(0, 2);
                    unused.removeIf(suitedCards::contains);
                    return new HandWeight(Combination.StraightFlush, i, unused);
                }
            }
        }

        return null;
    }

    private static HandWeight isKare(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 4, Combination.Kare);

    }

    private static HandWeight isFullHouse(List<String> cardsList) {
        HandWeight setPart = searchMaxRankOfStackedCards(cardsList, 3, Combination.Set);
        if (setPart == null) return null;
        int setWeight = setPart.getWeight() * 100;
        List<String> remainsCards = cardsList
                .stream()
                .map(card -> {
                            if (parseRank(card.substring(0, card.length() - 1)) == setPart.getWeight()) return "0*";
                            return card;
                        }
                )
                .toList();
        HandWeight pairPart = searchMaxRankOfStackedCards(remainsCards, 2, Combination.OnePair);
        if (pairPart == null) return null;
        return new HandWeight(Combination.FullHouse, setWeight + pairPart.getWeight(), pairPart.getUnusedCard());
    }


    private static HandWeight isFlush(List<String> cardsList) {
        List<String> suits = List.of("C", "D", "H", "S");

        for (String suit : suits) {
            List<String> suitedCards = cardsList.stream()
                    .filter(card -> card.endsWith(suit))
                    .toList();
            if (suitedCards.size() < 5) continue;
            Integer weight = suitedCards
                    .stream()
                    .map(card -> card.substring(0, card.length() - 1))
                    .map(GoodDealer::parseRank)
                    .max(Integer::compareTo).get();
            List<String> unused = cardsList.subList(0, 2);
            unused.removeIf(suitedCards::contains);
            return new HandWeight(Combination.Flush, weight, unused);
        }
        return null;


    }

    private static HandWeight isStraight(List<String> cardsList) {
        List<String> ranks = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

        for (int i = ranks.size() - 1; i >= 5; i--) {
            List<String> straight = ranks.subList(i - 5, i);
            if (new HashSet<>(cardsList).containsAll(straight)) {
                List<String> unused = cardsList.subList(0, 2);
                unused.removeIf(straight::contains);
                return new HandWeight(Combination.Straight, i, unused);
            }
        }

        return null;
    }

    private static HandWeight isSet(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 3, Combination.Set);
    }


    private static HandWeight isTwoPair(List<String> cardsList) {
        HandWeight firstPart = searchMaxRankOfStackedCards(cardsList, 2, Combination.OnePair);
        if (firstPart == null) return null;
        int firstPairWeight = firstPart.getWeight() * 100;
        List<String> remainsCards = cardsList
                .stream()
                .map(card -> {
                            if (parseRank(card.substring(0, card.length() - 1)) == firstPart.getWeight()) return "0*";
                            return card;
                        }
                )
                .toList();
        HandWeight secondPair = searchMaxRankOfStackedCards(remainsCards, 2, Combination.OnePair);
        if (secondPair == null) return null;
        return new HandWeight(Combination.TwoPair, firstPairWeight + secondPair.getWeight(), secondPair.getUnusedCard());
    }

    private static HandWeight isOnePair(List<String> cardsList) {
        return searchMaxRankOfStackedCards(cardsList, 2, Combination.OnePair);
    }

    private static HandWeight isHighCard(List<String> cardsList) {
        Integer weight = cardsList.subList(0, 2).stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(GoodDealer::parseRank)
                .max(Integer::compareTo).orElse(0);

        return new HandWeight(Combination.HighCard, weight, cardsList.subList(0, 2));
    }

    private static HandWeight searchMaxRankOfStackedCards(List<String> cardsList, int amount, Combination combination) {
        List<Integer> ranksList = new ArrayList<>(cardsList
                .stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(GoodDealer::parseRank)
                .toList());
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Integer card : ranksList) {
            rankCount.put(card, rankCount.getOrDefault(card, 0) + 1);
        }
        Integer maxRank = rankCount.entrySet().stream()
                .filter(entry -> entry.getValue() == amount)
                .map(Map.Entry::getKey)
                .max(Integer::compare).orElse(0);

        if (maxRank == 0) return null;
        List<Integer> hand = ranksList.subList(0, 2);
        hand.removeIf(rank -> Objects.equals(rank, maxRank));
        return new HandWeight(combination,
                maxRank,
                hand.stream().map(rank -> rank.toString() + "*").toList());

    }


    /**
     * проверяет корректность карт на столе, возвращает список карт
     * 0-1 - рука игрока №1
     * 2-6 - общие карты
     * 7-8 - рука игрока №2
     */
    private List<String> checkBoard(Board board, int stage) throws InvalidPokerBoardException {

        //todo мб переписать это на цикл, в котором через рефлексию получать все гетеры и выполнять их
        List<String> cardsOnTable = parseBoard(board);
        if (stages.get(stage) != cardsOnTable.size())
            throw new InvalidPokerBoardException("the number of cards does not correspond to the stage of the game");
        if (parseCards(board.getPlayerOne()).size()!=2)
            throw new InvalidPokerBoardException("player one haven't 2 cards");
        if (parseCards(board.getPlayerTwo()).size()!=2)
            throw new InvalidPokerBoardException("player two haven't 2 cards");

        if (stage > 1) {
            if (parseCards(board.getFlop()).size()!=3)
                throw new InvalidPokerBoardException("flop haven't 3 cards");
        }

        if (stage > 2) {
            if (parseCards(board.getTurn()).size()!=1)
                throw new InvalidPokerBoardException("turn haven't 1 cards");
        }

        if (stage > 3) {
            if (parseCards(board.getRiver()).size()!=1)
                throw new InvalidPokerBoardException("river haven't 1 cards");
        }


        if (!(cardsOnTable.size() == new HashSet<>(cardsOnTable).size()))
            throw new InvalidPokerBoardException("cards on the board isnt unique");

        return cardsOnTable;
    }

    private List<String> parseBoard(Board board) {
        List<String> cardsOnTable = new ArrayList<>();
        cardsOnTable.addAll(parseCards(board.getPlayerOne()));
        cardsOnTable.addAll(parseCards(board.getFlop()));
        cardsOnTable.addAll(parseCards(board.getTurn()));
        cardsOnTable.addAll(parseCards(board.getRiver()));
        cardsOnTable.addAll(parseCards(board.getPlayerTwo()));
        boolean lol = !cards.containsAll(cardsOnTable);
        if (!cards.containsAll(cardsOnTable))
            throw new InvalidPokerBoardException("cards on table contains inappropriate symbols");
        return cardsOnTable;
    }


    private List<String> parseCards(String cardsInString) {
        if (cardsInString == null) return new ArrayList<>();
        List<String> cards = new ArrayList<>();
        int i = 0;
        if (cardsInString.length() < 2)
            throw new InvalidPokerBoardException("cards on table contains inappropriate symbols");
        while (i < cardsInString.length() - 1) {
            int rankLength = (cardsInString.charAt(i) == '1') ? 2 : 1;
            String card = cardsInString.substring(i, i + rankLength + 1);

            cards.add(card);
            i += rankLength + 1;
        }

        return cards;
    }

    public static Integer parseRank(String rankInString) {
        return switch (rankInString) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            default -> Integer.parseInt(rankInString);
        };
    }

    private String getCardAndRotate() {
        String card = this.cards.pop();
        this.cards.addLast(card);
        return card;
    }
}
