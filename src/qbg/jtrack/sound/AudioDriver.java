package qbg.jtrack.sound;

import java.io.Closeable;

/**
 * Interface for the audio subsystem
 */
public interface AudioDriver extends Closeable {

    /**
     * Ready the audio system
     */
    public void open();

    public void close();

    /**
     * Remove all registered operations from this system
     */
    public void removeAllOperations();

    /**
     * Register an operation with this system
     * @param operation
     */
    public void addOperation(SoundOperation operation);

    /**
     * Generate a number of frames of sound
     * @param frames
     */
    public void generateFrames(int frames);

}