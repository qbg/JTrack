package qbg.jtrack.sound;

/**
 * Common interface for all sound operations
 */
public interface SoundOperation {
    /**
     * Modifies a 16 sample buffer, applying the operation to it.
     * @param buffer
     */
    void fillBuffer(int[] buffer);
}
