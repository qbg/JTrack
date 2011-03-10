package qbg.jtrack.instruments;

import qbg.jtrack.Configurable;
import qbg.jtrack.SettingsBuffer;

/**
 * Basic multi-point linear envelope
 */
final public class Env implements Configurable {
    /**
     * The target levels
     */
    private double[] levels;
    /**
     * The level of the last point
     */
    private double level;
    /**
     * The number of ticks immediately before reaching a new level
     */
    private int[] ticks;
    /**
     * The position to hold
     */
    private int hold;
    /**
     * The current position
     */
    private int pos;
    /**
     * The number of ticks that have currently elapsed
     */
    private int numTicks;
    
    /**
     * Default constructor
     */
    public Env() {
        pos = 0;
        hold = 0;
        ticks = new int[]{0, 0};
        levels = new double[]{0.0, 0.0};
        level = 0;
    }
    
    /**
     * Set the envelope settings
     * @param hold The index of the hold point
     * @param levels The target volumes
     * @param ticks The delay (in seconds) before each target volume
     */
    public void setSettings(int hold, double[] levels, double[] ticks) {
        this.hold = hold;
        this.levels = levels.clone();
        this.ticks = new int[ticks.length];
        
        double error = 0;
        for (int i = 0; i < ticks.length; i++) {
            this.ticks[i] = (int)((ticks[i]+error)*44100/16);
            error = ticks[i] - ((double)(this.ticks[i]))*16/44100;
        }
    }
    
    /**
     * Set the ADSR curve for the envelope
     * @param attack Number of milliseconds for attack
     * @param decay Number of milliseconds for decay
     * @param sustain Sustain volume
     * @param release Number of milliseconds for release
     */
    public void setADSR(double attack, double decay, double sustain,
            double release) {
        double[] levelsArr = {0.0, 1.0, sustain, sustain, 0.0};
        double[] ticksArr = {0.0, attack/1000, decay/1000, 0.0, release/1000};
        setSettings(2, levelsArr, ticksArr);
    } 
    
    /**
     * Generate the next envelope (linear) volume level
     * @return A linear fraction volume
     */
    public double generate() {
        if (pos == ticks.length-1 || pos == hold) {
            return levels[pos];
        } 
        if (numTicks == ticks[pos+1]) {
            numTicks = 0;
            pos++;
            level = levels[pos];
            return levels[pos];
        }
        double complete = ((double)numTicks)/ticks[pos+1];
        numTicks++;
        return level*(1-complete)+levels[pos+1]*complete;
    }
    
    /**
     * Press the key
     */
    public void press() {
        pos = 0;
        numTicks = 0; 
        level = levels[0];
    }
    
    /**
     * Release the key
     */
    public void release() {
        level = generate();
        pos = hold+1;
        numTicks = 0;
    }
    
    /**
     * Return true if the envelope has reached the end
     * @return boolean
     */
    public boolean isFinished() {
        return pos == ticks.length-1;
    }

    @Override
    public Object invokeCommand(String command, Object... args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void loadSettings(SettingsBuffer sb) {
        int n = sb.getInt();
        levels = new double[n];
        ticks = new int[n];
        for (int i = 0; i < n; i++) {
            levels[i] = sb.getDouble();
            ticks[i] = sb.getInt();
        }
        hold = sb.getInt();
    }

    @Override
    public void saveSettings(SettingsBuffer sb) {
        int n = levels.length;
        sb.putInt(n);
        for (int i = 0; i < n; i++) {
            sb.putDouble(levels[i]);
            sb.putInt(ticks[i]);
        }
        sb.putInt(hold);
    }
}
