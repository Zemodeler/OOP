package Part2;

import java.util.ArrayList;
import java.util.List;

public class RewardProfile {
    private String typistName;
    private int cumulativePoints;
    private int latestPoints;
    private int wins;
    private List<String> titles;
    private List<String> badges;
    private String rankImpact;

    public RewardProfile(String typistName) {
        this.typistName = typistName;
        this.cumulativePoints = 0;
        this.latestPoints = 0;
        this.wins = 0;
        this.titles = new ArrayList<>();
        this.titles.add("Unranked");
        this.badges = new ArrayList<>();
        this.badges.add("None");
        this.rankImpact = "None";
    }

    public String getTypistName() {
        return typistName;
    }

    public int getCumulativePoints() {
        return cumulativePoints;
    }

    public int getLatestPoints() {
        return latestPoints;
    }

    public int getWins() {
        return wins;
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<String> getBadges() {
        return badges;
    }

    public String getRankImpact() {
        return rankImpact;
    }

    public void setCumulativePoints(int cumulativePoints) {
        this.cumulativePoints = cumulativePoints;
    }

    public void setLatestPoints(int latestPoints) {
        this.latestPoints = latestPoints;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setTitle(String title) {
        this.titles.add(title);
    }

    public void setBadges(String badges) {
        this.badges.add(badges);
    }

    public void setRankImpact(String rankImpact) {
        this.rankImpact = rankImpact;
    }
}
