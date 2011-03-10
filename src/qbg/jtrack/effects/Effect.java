package qbg.jtrack.effects;

import qbg.jtrack.sound.SoundOperation;

/**
 * Common interface for all effects
 */
public interface Effect extends SoundOperation {
    /**
     * Modify the 16 sample buffer in place with the result of applying the
     * effect to it.
     * @param buffer The buffer to modify
     */
    public void fillBuffer(int[] buffer);

}