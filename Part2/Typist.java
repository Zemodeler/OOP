/**
 *
 *
 *
 * @author Andrei Dodu
 * @version 2.0
 */

import java.awt.Color;

public class Typist
{
    private String name;  // Name of the Typist
    private String symbol; // Symbol that represents typist in the race
    private int progress; // Progress in the typist
    private boolean burnOut; // Y/N if typist is burnt out 
    private int burnoutTurnsRemaining; // For how long the typist is gonna be burnt out/ remaining
    private double accuracy; // accuracy of typist

    private Color color;
    private double speed;
    private double mystypeModifier;
    private double burnoutModifier;
    private int burnoutDurationModifier;
    private double extraTyping = 0.0;

    private boolean energyDrink = false;
    private boolean caffeineFlag = false;
    private boolean energyFlag = false;

    private int correctKeystrokes;
    private int mistypeCount;
    private int burnoutCount;
    private double startingAccuracy;

    private static final double ACCURACY     = 0.7;

    // Constructor of class Typist
    public Typist(String typistSymbol, String typistName, double typistAccuracy, Color typistColor, double typistSpeedBoost, double typistmystype, double typistburnout, int burnoutDuration, boolean energyDrink)
    {
        this.name = typistName;
        this.color = typistColor;
        this.speed = typistSpeedBoost;
        this.mystypeModifier = typistmystype;
        this.burnoutModifier = typistburnout;
        this.burnoutDurationModifier = burnoutDuration;
        this.energyDrink = energyDrink;

        this.progress = 0;
        this.burnOut = false;
        this.burnoutTurnsRemaining = 0;

        correctKeystrokes = 0;
        mistypeCount = 0;
        burnoutCount = 0;

        SetStartingAccuracy(ACCURACY);
        setSymbol(typistSymbol);
        setAccuracy(typistAccuracy);
    }


    // Methods of class Typist

    public void accumulateTyping() {
        extraTyping += getSpeed();

        while (extraTyping >= 1.0) {
            typeCharacter();
            extraTyping -= 1.0;
        }
    }

    public void burnOut(int turns)
    {
        burnOut = true;
        burnoutTurnsRemaining = turns;
    }

    public void recoverFromBurnout()
    {   
        burnoutTurnsRemaining -= 1; 
        if (getBurnoutTurnsRemaining() <= 0) {
            burnOut = false;
            burnoutTurnsRemaining= 0;
        }
    }

    // getters

    public double getAccuracy()
    {
        return accuracy;
    }

    public int getProgress()
    {
        return progress; 
    }

    public String getName()
    {
        return name; 
    }

    public String getSymbol()
    {
        return symbol;
    }

    public int getBurnoutTurnsRemaining()
    {
        return burnoutTurnsRemaining;
    }

    public Color getColour() {
        return color;
    }

    public double getSpeed() {
        return speed;
    }

    public double getMistypeChanceModifier() {
        return mystypeModifier;
    }

    public double getBurnoutChanceModifier() {
        return burnoutModifier;
    }

    public int getBurnoutDurationAdjustment() {
        return burnoutDurationModifier;
    }

    public boolean getCaffeineFlag() {
        return caffeineFlag;
    }

    public boolean getEnergyDrink() {
        return energyDrink;
    }

    public boolean getEnergyFlag() {
        return energyFlag;
    }

    public int getCorrectKeystrokes() {
        return correctKeystrokes;
    }

    public int getMistypeCount() {
        return mistypeCount;
    }

    public int getBurnoutCount() {
        return burnoutCount;
    }

    public double getStartingAccuracy() {
        return startingAccuracy;
    }

    public double getAccuracyPercentage() {
        int totalAttempts = correctKeystrokes + mistypeCount;

        if (totalAttempts == 0) {
            return 0.0;
        }

        return (correctKeystrokes * 100.0) / totalAttempts;
    }

    // setters

    public void setSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            this.symbol = "?";
        } else {
            this.symbol = symbol;
        }
    }

    public void setColour(Color colour) {
        if (colour != null) {
            this.color = colour;
        }
    }

    public void setSpeed(double speedBoost) {
        this.speed = speedBoost;
    }

    public void setMistypeChanceModifier(double mistypeChanceModifier) {
        this.mystypeModifier = mistypeChanceModifier;
    }

    public void setBurnoutChanceModifier(double burnoutChanceModifier) {
        this.burnoutModifier = burnoutChanceModifier;
    }

    public void setBurnoutDurationAdjustment(int burnoutDurationAdjustment) {
        this.burnoutDurationModifier = burnoutDurationAdjustment;
    }

    public void resetToStart() {
        progress = 0;
        burnoutTurnsRemaining = 0;
        burnOut = false;
        extraTyping = 0.0;

        startingAccuracy = getAccuracy();
        correctKeystrokes = 0;
        mistypeCount = 0;
        burnoutCount = 0;
    }

    public boolean isBurntOut() {
        return burnOut;
    }

    public void typeCharacter() {
        progress++;
    }

    public void slideBack(int amount) {
        if (amount > 0) {
            if (progress - amount < 0 ) {
                progress = 0;
            }else {
                progress = progress - amount;
            }
        }
    }

    public void setAccuracy(double newAccuracy) {
        if(newAccuracy < 0.0) {
            newAccuracy = 0.0;
        }else if (newAccuracy > 1.0) {
            newAccuracy = 1.0;
        }

        accuracy = Math.round(newAccuracy * 100.0) / 100.0;
    }

    public void setCaffeineFlag(boolean value) {
        caffeineFlag = value;
    }
    
    public void setEnergyDrink(boolean value) {
        energyDrink = value;
    }

    public void setEnergyFlag(boolean value) {
        energyFlag = value;
    }

    public void recordCorrectKeystroke() {
        correctKeystrokes++;
    }

    public void recordMistype() {
        mistypeCount++;
    }

    public void recordBurnout() {
        burnoutCount++;
    }

    public void SetStartingAccuracy(double value) {
        startingAccuracy = value;
    }

}
