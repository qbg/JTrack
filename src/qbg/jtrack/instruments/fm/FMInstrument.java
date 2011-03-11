package qbg.jtrack.instruments.fm;
import qbg.jtrack.Configurable;
import qbg.jtrack.SettingsBuffer;
import qbg.jtrack.instruments.Env;
import qbg.jtrack.instruments.Instrument;

/**
 * An FM instrument
 */
public final class FMInstrument implements Instrument, Configurable {
    /**
     * The operators
     */
    private final Op ops[];
    /**
     * The envelopes
     */
    private final Env envs[];
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
    private final int algorithm[];
    /**
     * True if we can skip computation
     */
    private boolean finished;
    /**
     * Indicates if the corresponding freq in freqs is fixed
     */
    private final boolean[] fixedFreqs;
    /**
     * If fixed, becomes the frequency for the corresponding op, otherwise is
     * the frequency multiplier
     */
    private final double[] freqs;
    /**
     * If true, the corresponding envelope's master volume will track the
     * instruments master volume.
     */
    private final boolean[] velScale;
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
        
        velScale = new boolean[6];
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
        for (int i = 0; i < 6; i++) {
            if (velScale[i]) {
                envs[i].setVol(vol);
            }
        }
        this.vol = (int)(vol * 32768);
    }

    public void release() {
        finished = false;
        for (int i = 0; i < 6; i++) {
            envs[i].release();
        }
    }

    @Override
    public Object invokeCommand(String command, Object... args) {
        if (command.equals("adsr")) {
            envs[(Integer)args[0]].setADSR((Double)args[1], (Double)args[2],
                    (Double)args[3], (Double)args[4]);
            return null;
        } else if (command.equals("op")) {
            freqs[(Integer)args[0]] = (Double)args[1];
            fixedFreqs[(Integer)args[0]] = (Boolean)args[2];
            return null;
        } else if (command.equals("clear-connections")) {
            for (int i = 0; i < 6*7; i++) {
                algorithm[i] = 0;
            }
            return null;
        } else if (command.equals("connect")) {
            algorithm[((Integer)args[0])*7+((Integer)args[1])] =
                (int)(((Double)args[2])*32768);
            return null;
        } else if (command.equals("vel-scale")) {
            velScale[(Integer)args[0]] = (Boolean)args[1];
        }
        return null;
    }

    @Override
    public void loadSettings(SettingsBuffer sb) {
        for (int i = 0; i < 6; i++) {
            envs[i].loadSettings(sb);
            freqs[i] = sb.getDouble();
            fixedFreqs[i] = sb.getBoolean();
            velScale[i] = sb.getBoolean();
        }
        for (int i = 0; i < 6*7; i++) {
            algorithm[i] = sb.getInt();
        }
    }

    @Override
    public void saveSettings(SettingsBuffer sb) {
        for (int i = 0; i < 6; i++) {
            envs[i].saveSettings(sb);
            sb.putDouble(freqs[i]);
            sb.putBoolean(fixedFreqs[i]);
            sb.putBoolean(velScale[i]);
        }
        for (int i = 0; i < 6*7; i++) {
            sb.putInt(algorithm[i]);
        }
    }
}
