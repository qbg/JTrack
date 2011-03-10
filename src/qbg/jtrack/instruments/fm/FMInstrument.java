package qbg.jtrack.instruments.fm;
import qbg.jtrack.instruments.Env;
import qbg.jtrack.instruments.Instrument;

/**
 * An FM instrument
 */
final class FMInstrument implements Instrument {
    /**
     * The operators
     */
    private final Op ops[];
    /**
     * The envelopes
     */
    public final Env envs[];
    /**
     * The mix buffer
     */
    private final int mix[];
    /**
     * The input buffer
     */
    private final int input[];
    /**
     * The algorithm. The i*7+j's item sends op i's output to slot j (mix is
     * slot 6).
     */
    public final int algorithm[];
    /**
     * True if we can skip computation
     */
    private boolean finished;
    /**
     * Indicates if the corresponding freq in freqs is fixed
     */
    public final boolean[] fixedFreqs;
    /**
     * If fixed, becomes the frequency for the corresponding op, otherwise is
     * the frequency multiplier
     */
    public final double[] freqs;
    /**
     * The global volume
     */
    private int vol;
    
    /**
     * Construct an FMInstrument with undetermined settings
     */
    public FMInstrument() {
        ops = new Op[6];
        for (int i = 0; i < 6; i++) {
            ops[i] = new Op();
        }
        
        envs = new Env[6];
        for (int i = 0; i < 6; i++) {
            envs[i] = new Env();
        }
        
        mix = new int[7];
        input = new int[6];
        algorithm = new int[6*7];
        fixedFreqs = new boolean[6];
        freqs = new double[6];
        finished = false;
        vol = 32768;
    }
    
    public void fillBuffer(int buffer[]) {
        // Early exit
        if (finished) {
            for (int i = 0; i < 16; i++) {
                buffer[i] = 0;
            }
            return;
        }
        
        // Generate envelopes
        boolean newFinished = true;
        for (int i = 0; i < 6; i++) {
            ops[i].setVol(envs[i].generate());
            newFinished &= envs[i].isFinished();
        }
        finished = newFinished;
        
        // Perform algorithm, filling buffer
        for (int sample = 0; sample < 16; sample++) {
            for (int i = 0; i < 6; i++) {
                int val = ops[i].generate(input[i]);
                for (int j = 0; j < 7; j++) {
                    if (algorithm[i*7 + j] != 0) {
                        mix[j] += ((val * algorithm[i*7 + j]) >> 15);
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                input[i] = mix[i];
                mix[i] = 0;
            }
            buffer[sample] = (mix[6] * vol) >> 15;
            mix[6] = 0;
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public void press() {
        finished = false;
        for (int i = 0; i < 6; i++) {
            envs[i].press();
        }
    }
    
    public void press(double freq) {
        setFreq(freq);
        press();
    }
    
    public void press(double freq, double volume) {
        setFreq(freq);
        setVol(volume);
        press();
    }
    
    public void setFreq(double freq) {
        for (int i = 0; i < 6; i++) {
            if (fixedFreqs[i]) {
                ops[i].setFreq(freqs[i]);
            } else {
                ops[i].setFreq(freqs[i]*freq);
            }
        }
    }
    
    public void setVol(double vol) {
        this.vol = (int)(vol*32768);
    }

    public void release() {
        finished = false;
        for (int i = 0; i < 6; i++) {
            envs[i].release();
        }
    }
}
