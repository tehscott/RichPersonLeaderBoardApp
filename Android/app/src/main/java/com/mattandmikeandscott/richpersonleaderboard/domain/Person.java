package com.mattandmikeandscott.richpersonleaderboard.domain;

import java.util.ArrayList;

import static com.mattandmikeandscott.richpersonleaderboard.domain.PeopleQueryType.*;

public class Person {
    private String googleId;
    private String name;
    private int rank;
    private double wealth;
    private ArrayList<Rank> ranks;
    private ArrayList<Payment> payments;
    private ArrayList<Achievement> achievements;

    public Person(String googleId, String name, int rank, double wealth, ArrayList<Rank> ranks, ArrayList<Payment> payments, ArrayList<Achievement> achievements) {
        this.googleId = googleId;
        this.name = name;
        this.rank = rank;
        this.wealth = wealth;
        this.ranks = ranks;
        this.payments = payments;
        this.achievements = achievements;
    }

    public Rank getRankForRankType(RankType rankType) {
        for(Rank rank : getRanks()) {
            if(rank.getRankType() == rankType) {
                return rank;
            }
        }

        return null;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Rank> getRanks() {
        return ranks;
    }

    public void setRanks(ArrayList<Rank> ranks) {
        this.ranks = ranks;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }

    public ArrayList<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(ArrayList<Achievement> achievements) {
        this.achievements = achievements;
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
