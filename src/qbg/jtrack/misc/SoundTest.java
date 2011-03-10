package qbg.jtrack.misc;

import qbg.jtrack.effects.DriveEffect;
import qbg.jtrack.instruments.Instrument;
import qbg.jtrack.instruments.fm.FMInstrument;
import qbg.jtrack.sound.AudioDriver;
import qbg.jtrack.sound.JavaAudio;
import qbg.jtrack.sound.Rail;

/**
 * Sound usage test
 */
public class SoundTest {
    /**
     * Create a sample rail
     * @param au The audio unit to add the rail to
     * @return A rail
     */
    private static Instrument buildRail(AudioDriver au) {
        FMInstrument cfg = new FMInstrument();
        cfg.invokeCommand("adsr", 0, 50.0, 50.0, 0.7, 200.0);
        cfg.invokeCommand("adsr", 1, 1.0, 10000.0, 0.1, 1000.0);
        cfg.invokeCommand("op", 0, 1.0, false);
        cfg.invokeCommand("op", 1, 0.8, false);
        cfg.invokeCommand("connect", 0, 6, 1.0);
        cfg.invokeCommand("connect", 1, 0, 0.8);
        cfg.invokeCommand("connect", 1, 1, 0.1);
        
        DriveEffect drive = new DriveEffect();
        drive.setDrive(10);
        au.addOperation(new Rail(cfg, drive));
        return cfg;
    }
    
    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        AudioDriver au = new JavaAudio();
        au.open();
        Instrument instr = buildRail(au);
        for (int i = 0; i < 20; i++) {
            instr.press(440 + i * 100, 1);
            au.generateFrames(1500);
            instr.release();
            au.generateFrames(500);
        }
        au.generateFrames(4000);
        au.close();
    }
    
}
