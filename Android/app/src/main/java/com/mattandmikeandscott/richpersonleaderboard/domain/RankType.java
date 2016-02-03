package com.mattandmikeandscott.richpersonleaderboard.domain;

public enum RankType {
    AllTime(1), Year(2), Month(3), Week(4), Day(5);

    private final int rankType;
    RankType(int rankType) { this.rankType = rankType; }
    public int getValue() { return rankType; }
    public String getName() {
        switch(rankType) {
            case 1:
                return "All Time";
            case 2:
                return "Year";
            case 3:
                return "Month";
            case 4:
                return "Week";
            case 5:
                return "Day";
        }

        return "";
    }

    public static RankType getRankType(int rankType) {
        switch(rankType) {
            case 1:
                return AllTime;
            case 2:
                return Year;
            case 3:
                return Month;
            case 4:
                return Week;
            case 5:
                return Day;
        }

        return AllTime;
    }

    public static RankType getRankTypeFromName(String name) {
        switch(name) {
            case "All Time":
                return RankType.AllTime;

            case "Year":
                return RankType.Year;

            case "Month":
                return RankType.Month;

            case "Week":
                return RankType.Week;

            case "Day":
                return RankType.Day;
        }

        return RankType.AllTime;
    }
}