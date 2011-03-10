package qbg.jtrack.effects;

/**
 * My drive effect from Winamp
 * 
 * Dynamically amplify the sound. Keeps a slightly low passed version of its
 * output. Amplification of the signal is linearly determined from the low 
 * passed version's amplitude; when it is zero, the amplification is drive; when
 * it is full, the output is muted.
 */
public class DriveEffect implements Effect {
    /**
     * The internal state of the filter
     */
    private int sum;
    /**
     * The current drive level (65536 is 1x)
     */
    private int drive;
    /**
     * 1/20th
     */
    private final static int DIV_SMALL = 65536/20;
    /**
     * 19/20ths
     */
    private final static int DIV_LARGE = 65536-DIV_SMALL;
    
    public void fillBuffer(int[] buffer) {
        for (int i = 0; i < 16; i++) {
            long val = buffer[i];
            int mul = (int)(drive - ((val * drive) >> 16));
            val = (val * mul) >> 16;
            val = val > 32767 ? 32767 :
                  val < -32767 ? -32767 :
                  val;
            int val2 = (int)(val < 0 ? -val : val);
            sum = ((sum * DIV_LARGE) >> 16) + ((val2 * DIV_SMALL) >> 16);
            buffer[i] = (int)val;
        }
    }
    
    /**
     * Set the current drive level.
     * 
     * Typically the drive level would be in the range 1-10.
     * 
     * @param drive The drive level
     */
    public void setDrive(double drive) {
        this.drive = (int)(drive * 65536);
    }
}
