package qbg.jtrack.sound;

import java.io.Closeable;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * Wrapper around Java audio
 */
public class JavaAudio implements Closeable, AudioDriver {
    /**
     * The line the system uses
     */
    private SourceDataLine line;
    /**
     * Array of registered operations
     */
    private SoundOperation[] operations = new SoundOperation[0];
    /**
     * Common buffer
     */
    private int[] buffer = new int[16];
    /**
     * Mix buffer
     */
    private int[] mix = new int[16];
    /**
     * Buffered used by sound I/O
     */
    private byte[] target = new byte[2048];
    
    /**
     * Ready the audio system
     */
    public void open() {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException("Line type not supported");
        }
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void close() {
        line.stop();
        line.close();
    }
    
    /**
     * Remove all registered operations from this system
     */
    public void removeAllOperations() {
        operations = new SoundOperation[0];
    }

    /**
     * Register an operation with this system
     * @param operation
     */
    public void addOperation(SoundOperation operation) {
        int len = operations.length;
        operations = Arrays.copyOf(operations, len+1);
        operations[len] = operation;
    }
    
    /**
     * Generate a number of frames of sound
     * @param frames
     */
    public void generateFrames(int frames) {
        int idx = 0;
        for (int frame = 0; frame < frames; frame++) {
            for (int op = 0; op < operations.length; op++) {
                operations[op].fillBuffer(buffer);
                for (int i = 0; i < 16; i++) {
                    mix[i] += buffer[i];
                }
            }
            for (int i = 0; i < 16; i++) {
                int val = mix[i];
                val = val < -32767 ? -32767 :
                      val > 32767 ? 32767 :
                      val;
                mix[i] = 0;
                target[idx++] = (byte)val;
                target[idx++] = (byte)(val>>8);
            }
            if (idx == 2048) {
                line.write(target, 0, 2048);
                idx = 0;
            }
        }
        line.write(target, 0, idx);
    }
}
