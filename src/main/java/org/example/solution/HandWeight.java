package org.example.solution;

import java.util.List;

public class HandWeight implements Comparable<HandWeight> {
    private Combination combination;
    private int weight;
    private List<String> unusedCard;

    public HandWeight() {
        this.combination = Combination.HighCard;
        this.weight = 0;
    }

    public HandWeight(Combination combination, int weight) {
        this.combination = combination;
        this.weight = weight;
    }

    public HandWeight(Combination combination, int weight, List<String> unusedCard) {
        this.combination = combination;
        this.weight = weight;
        this.unusedCard = unusedCard;
    }

    public Combination getCombination() {
        return combination;
    }

    public void setCombination(Combination combination) {
        this.combination = combination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<String> getUnusedCard() {
        return unusedCard;
    }

    public void setUnusedCard(List<String> unusedCard) {
        this.unusedCard = unusedCard;
    }

    @Override
    public int compareTo(HandWeight other) {
        int combinationComparison = this.combination.compareTo(other.combination);
        if (combinationComparison != 0) return combinationComparison;
        combinationComparison = Integer.compare(this.weight, other.weight);
        if (combinationComparison != 0) return combinationComparison;
        if (this.unusedCard.isEmpty() && other.unusedCard.isEmpty()) {
            return 0;
        }

        int size = Math.min(this.unusedCard.size(), other.unusedCard.size());
        List<Integer> firstHand = this.unusedCard.stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(GoodDealer::parseRank)
                .sorted()
                .toList();
        List<Integer> secondhand = other.unusedCard.stream()
                .map(card -> card.substring(0, card.length() - 1))
                .map(GoodDealer::parseRank)
                .sorted()
                .toList();
        for (int i = 0; i < size; i++) {
            int compareResult = this.unusedCard.get(i).compareTo(other.unusedCard.get(i));
            if (compareResult != 0) {
                return compareResult;
            }
        }
        return Integer.compare(this.unusedCard.size(), other.unusedCard.size());
    }

    @Override
    public String toString() {
        return "HandWeight{" +
                "combination=" + combination +
                ", weight=" + weight +
                ", unusedCard=" + unusedCard +
                '}';
    }
}
