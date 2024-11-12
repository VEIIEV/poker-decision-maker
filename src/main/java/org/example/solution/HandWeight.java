package org.example.solution;

public class HandWeight implements Comparable<HandWeight> {
    private Combination combination;
    private int weight;


    public HandWeight() {
        this.combination = Combination.HighCard;
        this.weight = 0;
    }

    public HandWeight(Combination combination, int weight) {
        this.combination = combination;
        this.weight = weight;
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


    @Override
    public int compareTo(HandWeight other) {
        int combinationComparison = this.combination.compareTo(other.combination);
        if (combinationComparison != 0) {
            return combinationComparison;
        }

        return Integer.compare(this.weight, other.weight);
    }
}
