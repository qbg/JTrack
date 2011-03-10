package qbg.jtrack.instruments.fm;

/**
 * An operator
 */
final class Op {
    /**
     * Sine wave waveform
     */
    final private int sine[];

    /**
     * Default constructor
     */
    public Op() {
        sine = new int[1<<16];
        for (int i = 0; i < sine.length; i++) {
            double val = Math.sin(2*Math.PI*i/sine.length);
            sine[i] = (int)(val * 32767);
        }
    }

    /**
     * The current phase of the sine wave
     */
    private long phase;
    /**
     * The phase increment (frequency) of the op
     */
    private long pIncr;
    /**
     * Current output volume
     */
    private int vol;
    
    /**
     * Generate the next sample, given the current input sample
     * @param inputVal The input sample
     * @return The next sample
     */
    public int generate(int inputVal) {
        long pval = (phase + 0xFFFFl*inputVal) & 0xFFFFFFFFl;
        int sval = (sine[(int)(pval >> 16)] * vol) >> 15;
        phase += pIncr;
        return sval;
    }
    
    /**
     * Set the volume of the operator
     * @param vPercent The linear multiplier
     */
    public void setVol(double vPercent) {
        vol = (int)(vPercent*(1<<15));
    }
    
    /**
     * Set the frequency of the operator
     * @param freq The frequency
     */
    public void setFreq(double freq) {
        pIncr = (long)(freq/44100*(1l<<32));
    }
}
