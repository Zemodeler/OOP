

public class RaceHistoryRecord {
    private int raceNumber;
    private int position;
    private double wpm;
    private double accuracyPercentage;
    private int burnoutCount;

    public RaceHistoryRecord(int raceNumber, int position, double wpm, double accuracyPercentage, int burnoutCount) {
        this.raceNumber = raceNumber;
        this.position = position;
        this.wpm = wpm;
        this.accuracyPercentage = accuracyPercentage;
        this.burnoutCount = burnoutCount;
    }

    public int getRaceNumber() {
        return raceNumber;
    }

    public int getPosition() {
        return position;
    }

    public double getWpm() {
        return wpm;
    }

    public double getAccuracyPercentage() {
        return accuracyPercentage;
    }

    public int getBurnoutCount() {
        return burnoutCount;
    }
}
