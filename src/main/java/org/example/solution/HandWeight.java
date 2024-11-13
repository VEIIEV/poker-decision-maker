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

    public HandWeight(Combination combination, int i, List<String> unusedCard) {
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
        if (combinationComparison != 0) {
            return combinationComparison;
        }

        return Integer.compare(this.weight, other.weight);
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
