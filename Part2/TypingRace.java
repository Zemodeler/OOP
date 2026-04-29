package Part2;
import java.util.concurrent.TimeUnit;
import java.awt.Color;
import java.lang.Math;
/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 *
 **/

public class TypingRace
{   
    private String passage;

    private Typist[] typists;

    private boolean autocorrectEnabled;
    private boolean caffeineModeEnabled;
    private boolean nightShiftEnabled;
    

    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT_ORIGINAL   = 2;
    private static final int    BURNOUT_DURATION     = 3;
    private static final double ACCURACY     = 0.6;
    private static final double SPEED = 1.0; 
    private static final double CAFFEINE_BOOST = 0.2;
    private static final double CAFFEINE_BURNOUT = 0.2;
    
    private boolean finished;
    private Typist winner;
    private boolean caffeineFlag = false;
    private double winnerAccuracyBeforeBonus;
    private int turnNumber = 0;

    

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(String passage, int seatCount, boolean autocorrectEnabled, boolean caffeineModeEnabled, boolean nightShiftEnabled)
    {
        this.passage = passage;

        if(seatCount < 2 ) {
            seatCount = 2;
        }else if (seatCount > 6) {
            seatCount = 6;
        }

        this.autocorrectEnabled = autocorrectEnabled;
        this.caffeineModeEnabled = caffeineModeEnabled;
        this.nightShiftEnabled = nightShiftEnabled;

        typists = new Typist[seatCount];
        finished = false;
        winner = null;
        turnNumber = 1;
    }

    public void createTypist(String symbol, String name ,double accuracyBoost,Color color, double typistSpeedBoost,double typistBurnoutMod,double typistMistypeMod,int burnoutDurationMod, int seatCount) {
        double accuracy = roundTwoDecimals(ACCURACY + accuracyBoost);

        if (nightShiftEnabled) {
            accuracy = roundTwoDecimals(accuracy - 0.20);
        }

        double typistSpeed = roundTwoDecimals(SPEED+typistSpeedBoost);
        double typistMistype = roundTwoDecimals(MISTYPE_BASE_CHANCE + typistMistypeMod);
        int burnoutDuration = BURNOUT_DURATION + burnoutDurationMod;
        double typistBurnout = typistBurnoutMod;
        

        typists[seatCount-1] = new Typist(symbol, name, accuracy, color, typistSpeed, typistMistype, typistBurnout, burnoutDuration);
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */


    public void startRace()
    {   
        finished = false;
        winner = null;

        while (!finished){
            advanceOneTurn();
            turnNumber += 1;

            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        double OldAccuracy = winner.getAccuracy();
        winner.setAccuracy(OldAccuracy+0.02); // As Shown in the image of the simulation, 0.02 improvement.

    }

    public void advanceOneTurn() {
        if (finished) {
            return;
        }
        
        turnNumber++;

        for(int i = 0; i < typists.length; i++) {
            if (typists[i] != null) {
                advanceTypist(typists[i]);
            }
        }

        checkForWinner();
    }

    private void checkForWinner() {
        for (int i = 0; i < typists.length; i++) {
            if (typists[i] != null && typists[i].getProgress() >= passage.length()) {
                winner = typists[i];
                winnerAccuracyBeforeBonus = winner.getAccuracy();

                winner.setAccuracy(winner.getAccuracy() + 0.02);

                finished = true;
                return;
            }
        }
    }


    private void advanceTypist(Typist theTypist)
    {

        if(caffeineModeEnabled && turnNumber <= 10 && !caffeineFlag) {
            theTypist.setSpeed(roundTwoDecimals(theTypist.getSpeed() + CAFFEINE_BOOST));
            caffeineFlag = true;

        }else if (caffeineModeEnabled && turnNumber == 10) {
            theTypist.setSpeed(roundTwoDecimals(theTypist.getSpeed()- CAFFEINE_BOOST));
            theTypist.setBurnoutChanceModifier(roundTwoDecimals(theTypist.getBurnoutChanceModifier() + CAFFEINE_BURNOUT ));
        }

        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        if (Math.random()* (1- theTypist.getMistypeChanceModifier()) < (1.0 - theTypist.getAccuracy()))
        {      
            int SLIDE_BACK_AMOUNT = SLIDE_BACK_AMOUNT_ORIGINAL;
            
            if(autocorrectEnabled == true) {
                SLIDE_BACK_AMOUNT = SLIDE_BACK_AMOUNT_ORIGINAL / 2;
            }
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() * (1-theTypist.getBurnoutChanceModifier()) < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {   
            int burnoutModifier = theTypist.getBurnoutDurationAdjustment();
            theTypist.burnOut(BURNOUT_DURATION+burnoutModifier);
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.02);
        }
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public Typist getTypist(int index) {
        if (index >= 0 && index < typists.length) {
            return typists[index];
        }

        return null;
    }

    public boolean isFinished() {
        return finished;
    }

    public Typist getWinner() {
        return winner;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public double getWinnerAccuracyBeforeBonus() {
        return winnerAccuracyBeforeBonus;
    }

    public int getPassageLength() {
        return passage.length();
    }

    public String getPassage() {
        return passage;
    }

    public static void main(String[] args) {

    }
}
