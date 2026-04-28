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
public class Typist
{
    private String name;  // Name of the Typist
    private char symbol; // Symbol that represents typist in the race
    private int progress; // Progress in the typist
    private boolean burnOut; // Y/N if typist is burnt out 
    private int burnoutTurnsRemaining; // For how long the typist is gonna be burnt out/ remaining
    private double accuracy; // accuracy of typist



    // Constructor of class Typist
    public Typist(char typistSymbol, String typistName, double typistAccuracy)
    {
        name = typistName;
        progress = 0;
        burnOut = false;
        burnoutTurnsRemaining = 0;
        symbol = typistSymbol;
        
        setAccuracy(typistAccuracy);
    }


    // Methods of class Typist

    /**
     * Sets this typist into a burnout state for a given number of turns.
     * A burnt-out typist cannot type until their burnout has worn off.
     *
     * @param turns the number of turns the burnout will last
     */
    public void burnOut(int turns)
    {
        burnOut = true;
        burnoutTurnsRemaining = turns;
    }

    /**
     * Reduces the remaining burnout counter by one turn.
     * When the counter reaches zero, the typist recovers automatically.
     * Has no effect if the typist is not currently burnt out.
     */
    public void recoverFromBurnout()
    {
        if (getBurnoutTurnsRemaining() == 0) {
            burnOut = false;
        }else {
            burnoutTurnsRemaining -= 1; 
        }
    }

    /**
     * Returns the typist's accuracy rating.
     *
     * @return accuracy as a double between 0.0 and 1.0
     */
    public double getAccuracy()
    {
        return accuracy;
    }

    /**
     * Returns the typist's current progress through the passage.
     * Progress is measured in characters typed correctly so far.
     * Note: this value can decrease if the typist mistypes.
     *
     * @return progress as a non-negative integer
     */
    public int getProgress()
    {
        return progress; 
    }

    /**
     * Returns the name of the typist.
     *
     * @return the typist's name as a String
     */
    public String getName()
    {
        return name; 
    }

    /**
     * Returns the character symbol used to represent this typist.
     *
     * @return the typist's symbol as a char
     */
    public char getSymbol()
    {
        return symbol;
    }

    /**
     * Returns the number of turns of burnout remaining.
     * Returns 0 if the typist is not currently burnt out.
     *
     * @return burnout turns remaining as a non-negative integer
     */
    public int getBurnoutTurnsRemaining()
    {
        return burnoutTurnsRemaining;
    }

    /**
     * Resets the typist to their initial state, ready for a new race.
     * Progress returns to zero, burnout is cleared entirely.
     */
    public void resetToStart()
    {
        progress = 0;
        burnoutTurnsRemaining = 0;
        burnOut = false;

    }

    /**
     * Returns true if this typist is currently burnt out, false otherwise.
     *
     * @return true if burnt out
     */
    public boolean isBurntOut()
    {
        return burnOut;
    }

    /**
     * Advances the typist forward by one character along the passage.
     * Should only be called when the typist is not burnt out.
     */
    public void typeCharacter()
    {
        progress++;
    }

    /**
     * Moves the typist backwards by a given number of characters (a mistype).
     * Progress cannot go below zero — the typist cannot slide off the start.
     *
     * @param amount the number of characters to slide back (must be positive)
     */
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

    /**
     * Sets the accuracy rating of the typist.
     * Values below 0.0 should be set to 0.0; values above 1.0 should be set to 1.0.
     *
     * @param newAccuracy the new accuracy rating
     */
    public void setAccuracy(double newAccuracy)
    {
        if(newAccuracy < 0.0) {
            newAccuracy = 0.0;
        }else if (newAccuracy > 1.0) {
            newAccuracy = 1.0;
        }

        accuracy = newAccuracy;
    }

    /**
     * Sets the symbol used to represent this typist.
     *
     * @param newSymbol the new symbol character
     */
    public void setSymbol(char newSymbol)
    {
        symbol = newSymbol;
    }

}
