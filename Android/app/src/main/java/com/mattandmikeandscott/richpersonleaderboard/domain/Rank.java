package com.mattandmikeandscott.richpersonleaderboard.domain;

public class Rank {
    private RankType rankType;
    private int rank;
    private double wealth;

    public Rank(RankType rankType, int rank, double wealth) {
        this.rankType = rankType;
        this.rank = rank;
        this.wealth = wealth;
    }

    public RankType getRankType() {
        return rankType;
    }

    public void setRankType(RankType rankType) {
        this.rankType = rankType;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getWealth() {
        return wealth;
    }

    public void setWealth(double wealth) {
        this.wealth = wealth;
    }
}
