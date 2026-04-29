/**
 * The Class Typist is the class that defines one of the members of the competition, a Typist as the name entails,
 * this class gives the 6 variables that all typists have and the methods needed to access these values.  
 *
 * Starter code generously abandoned by Ty Posaurus, your predecessor,
 * who typed with two fingers and considered that "good enough".
 * He left a sticky note: "the slide-back thing is optional probably".
 * It is not optional. Good luck.
 *
 * @author Andrei Dodu
 * @version 1.0
 */

package Part2;
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
    private int speedBoost;
    private double mystypeModifier;
    private double burnoutModifier;
    private int burnoutDuratinModifier;

    

    // Constructor of class Typist
    public Typist(String typistSymbol, String typistName, double typistAccuracy, Color typistColor, int typistSpeedBoost, double typistmystype, double typistburnout, int burnoutDuration)
    {
        this.name = typistName;
        this.color = typistColor;
        this.speedBoost = typistSpeedBoost;
        this.mystypeModifier = typistmystype;
        this.burnoutModifier = typistburnout;
        this.burnoutDuratinModifier = burnoutDuration;

        this.progress = 0;
        this.burnOut = false;
        this.burnoutTurnsRemaining = 0;

        setSymbol(typistSymbol);
        setAccuracy(typistAccuracy);
    }


    // Methods of class Typist

    public void burnOut(int turns)
    {
        burnOut = true;
        burnoutTurnsRemaining = turns;
    }

    public void recoverFromBurnout()
    {
        if (getBurnoutTurnsRemaining() == 0) {
            burnOut = false;
        }else {
            burnoutTurnsRemaining -= 1; 
        }
    }

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

    public int getSpeedBoost() {
        return speedBoost;
    }

    public double getMistypeChanceModifier() {
        return mystypeModifier;
    }

    public double getBurnoutChanceModifier() {
        return burnoutModifier;
    }

    public int getBurnoutDurationAdjustment() {
        return burnoutDuratinModifier;
    }

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

    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
    }

    public void setMistypeChanceModifier(double mistypeChanceModifier) {
        this.mystypeModifier = mistypeChanceModifier;
    }

    public void setBurnoutChanceModifier(double burnoutChanceModifier) {
        this.burnoutModifier = burnoutChanceModifier;
    }

    public void setBurnoutDurationAdjustment(int burnoutDurationAdjustment) {
        this.burnoutDuratinModifier = burnoutDurationAdjustment;
    }

    public void resetToStart()
    {
        progress = 0;
        burnoutTurnsRemaining = 0;
        burnOut = false;

    }

    public boolean isBurntOut()
    {
        return burnOut;
    }

    public void typeCharacter()
    {
        progress++;
    }

    public void slideBack(int amount)
    {
        if (amount > 0) {
            if (progress - amount < 0 ) {
                progress = 0;
            }else {
                progress = progress - amount;
            }
        }
    }

    public void setAccuracy(double newAccuracy)
    {
        if(newAccuracy < 0.0) {
            newAccuracy = 0.0;
        }else if (newAccuracy > 1.0) {
            newAccuracy = 1.0;
        }

        accuracy = Math.round(newAccuracy * 100.0) / 100.0;
    }

}
