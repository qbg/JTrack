package qbg.jtrack.misc;

import qbg.jtrack.effects.DriveEffect;
import qbg.jtrack.instruments.Instrument;
import qbg.jtrack.instruments.fm.Config;
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
        Config cfg = new Config();
        cfg.setADSR(0, 50, 50, 0.7, 200);
        cfg.setADSR(2, 1, 10000, 0.1, 1000);
        cfg.setOp(0, 1, false);
        cfg.setOp(2, 0.8, false);
        cfg.connect(0, 6, 1);
        cfg.connect(2, 0, 0.7);
        cfg.connect(2, 2, 1);
        
        DriveEffect drive = new DriveEffect();
        drive.setDrive(10);
        au.addOperation(new Rail(cfg.getInstrument(), drive));
        return cfg.getInstrument();
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
            instr.press(Math.pow(2,i), 1);
            au.generateFrames(1500);
            instr.release();
            au.generateFrames(500);   
        }
        au.generateFrames(4000);
        au.close();
    }
    
}
