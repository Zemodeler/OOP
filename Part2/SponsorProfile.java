

import java.util.ArrayList;
import java.util.List;

public class SponsorProfile {
    private String typistName;
    private String sponsorName;
    private int totalEarnings;
    private int latestEarnings;
    private int upgradesPurchased;
    private List<String> upgrades;

    public SponsorProfile(String typistName) {
        this.typistName = typistName;
        this.sponsorName = "No Sponsor";
        this.totalEarnings = 0;
        this.latestEarnings = 0;
        this.upgradesPurchased = 0;
        this.upgrades = new ArrayList<>();
    }

    public String getTypistName() {
        return typistName;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public int getTotalEarnings() {
        return totalEarnings;
    }

    public int getLatestEarnings() {
        return latestEarnings;
    }

    public int getUpgradesPurchased() {
        return upgradesPurchased;
    }

    public List<String> getUpgrades() {
        return upgrades;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public void setTotalEarnings(int totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public void setLatestEarnings(int latestEarnings) {
        this.latestEarnings = latestEarnings;
    }

    public void setUpgradesPurchased(int upgradesPurchased) {
        this.upgradesPurchased = upgradesPurchased;
    }

    public void addUpgrade(String upgrade) {
        if (!upgrades.contains(upgrade)) {
            upgrades.add(upgrade);
            upgradesPurchased++;
        }
    }
}