package qbg.jtrack.instruments.fm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qbg.jtrack.instruments.Instrument;

/**
 * Configuration class for FM instruments
 */
public class Config {
    /**
     * The instance of FMInstrument being configured
     */
    private final FMInstrument instr;
    private final OpSettings[] settings = new OpSettings[6];
    
    /**
     * Create a configuration for a new FM instrument
     */
    public Config() {
        instr = new FMInstrument();
        for (int i = 0; i < 6; i++) {
            settings[i].applySettings(i);
        }
    }
 
    /**
     * Get the instrument being configured by this object
     * @return The instrument
     */
    public Instrument getInstrument() {
        return instr;
    }
    
    /**
     * Set the ADSR curve for the op at index.
     * @param index The index of the op
     * @param attack Number of milliseconds for attack
     * @param decay Number of milliseconds for decay
     * @param sustain Sustain volume
     * @param release Number of milliseconds for release
     */
    public void setADSR(int index, double attack, double decay, double sustain,
                        double release) {
        settings[index].attack = attack;
        settings[index].decay = decay;
        settings[index].sustain = sustain;
        settings[index].release = release;
        settings[index].applySettings(index);
    }
    
    /**
     * Set the frequency/multiplier of an op
     * @param index The index of the op
     * @param freqSpec The multiplier/frequency of the op
     * @param fixed If true, freqSpec is a frequency; otherwise a multiplier
     */
    public void setOp(int index, double freqSpec, boolean fixed) {
        settings[index].freqSpec = freqSpec;
        settings[index].fixed = fixed;
        settings[index].applySettings(index);
    }
    
    /**
     * Clear all of the connections in the instrument
     */
    public void clearConnections() {
        for (int i = 0; i < 6*7; i++) {
            instr.algorithm[i] = 0;
        }
    }
    
    /**
     * Connect operator i to operator j with a given transfer volume. If 6 is
     * used for j, the operator will be connected to the output.
     * @param i
     * @param j
     * @param volume
     */
    public void connect(int i, int j, double volume) {
        instr.algorithm[i*7+j] = (int)(volume*32768);
    }

    public List<Double> saveSettings() {
        List<Double> res = new ArrayList<Double>();
        for (int i = 0; i < 6; i++) {
            settings[i].saveSettings(res);
        }
        for (int i = 0; i < 6*7; i++) {
            res.add((double)instr.algorithm[i]);
        }
        return res;
    }
    
    public void loadSettings(List<Double> settings) {
        Iterator<Double> view = settings.iterator();
        for (int i = 0; i < 6; i++) {
            this.settings[i].loadSettings(view);
        }
        for (int i = 0; i < 6*7; i++) {
            instr.algorithm[i] = (int)(double)view.next();
        }
    }
    
    private class OpSettings {
        public double attack = 1;
        public double decay = 1;
        public double sustain = 0;
        public double release = 1;
        public double freqSpec = 1;
        public boolean fixed = false;
        
        public void applySettings(int i) {
            instr.freqs[i] = freqSpec;
            instr.fixedFreqs[i] = fixed;
            instr.envs[i].setADSR(attack, decay, sustain, release);
        }
        
        public void saveSettings(List<Double> res) {
            res.add(attack);
            res.add(decay);
            res.add(sustain);
            res.add(release);
            res.add(freqSpec);
            res.add(fixed ? 1.0 : 0.0);
        }
        
        public void loadSettings(Iterator<Double> view) {
            attack = view.next();
            decay = view.next();
            sustain = view.next();
            release = view.next();
            freqSpec = view.next();
            fixed = view.next() == 1.0;
        }
    }
}
