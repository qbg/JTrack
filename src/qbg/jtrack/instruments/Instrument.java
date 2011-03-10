package qbg.jtrack.instruments;

import qbg.jtrack.sound.SoundOperation;

/**
 * Common interface for all instruments
 */
public interface Instrument extends SoundOperation {
    /**
     * Fill a 16 element buffer with samples
     * @param buffer The target for the samples
     */
    void fillBuffer(int buffer[]);
    /**
     * Return true if the instrument has finished providing output
     * @return boolean
     */
    boolean isFinished();
    /**
     * Press the key
     */
    void press();
    /**
     * Press the key corresponding to freq Hertz
     * @param freq The key's frequency in Hertz
     */
    void press(double freq);
    /**
     * Press the key corresponding to freq Hertz with velocity vol (0-1).
     * @param freq
     * @param vol
     */
    void press(double freq, double vol);
    /**
     * Set the frequency of the instrument
     * @param freq The frequency in Hertz
     */
    void setFreq(double freq);
    /**
     * Set the output volume of the instrument
     * @param vol The linear volume in the range 0-1
     */
    void setVol(double vol);
    /**
     * Release the key
     */
    void release();
}
