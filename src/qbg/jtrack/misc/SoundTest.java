package qbg.jtrack.misc;

import java.util.Arrays;
import java.util.List;

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
        cfg.invokeCommand("adsr", 0, 50.0, 50.0, 0.7, 2000.0);
        cfg.invokeCommand("adsr", 1, 1.0, 50.0, 0.8, 1500.0);
        cfg.invokeCommand("adsr", 2, 100.0, 100.0, 0.5, 1000.0);
        cfg.invokeCommand("vel-scale", 2, true);
        cfg.invokeCommand("op", 0, 1.0, false);
        cfg.invokeCommand("op", 1, 0.1, false);
        cfg.invokeCommand("op", 2, 1.2, false);
        cfg.invokeCommand("connect", 0, 6, 1.0);
        cfg.invokeCommand("connect", 1, 0, 0.5);
        cfg.invokeCommand("connect", 1, 2, 0.8);
        cfg.invokeCommand("connect", 2, 0, 0.9);
        
        DriveEffect drive = new DriveEffect();
        drive.setDrive(2);
        au.addOperation(new Rail(cfg));
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
        List<Integer> notes = Arrays.asList(3, 10, 7, 10);
        playNotes(notes, 4, instr, au);
        notes = Arrays.asList(3, 10, 8, 10);
        playNotes(notes, 4, instr, au);
        notes = Arrays.asList(3, 10, 7, 10);
        playNotes(notes, 4, instr, au);
        au.generateFrames(12000);
        au.close();
    }
    
    /**
     * Play some notes
     * @param notes A list of note values (Concert A is 0)
     * @param times The number of times to play the notes
     * @param instr The instrument to play the notes on
     * @param au The AudioDriver for generating the sound
     */
    public static void playNotes(List<Integer> notes, int times,
            Instrument instr, AudioDriver au) {
        double n = times * notes.size();
        for (int j = 0; j < times; j++) {
            for (int i = 0; i < notes.size(); i++) {
                instr.press(440 * Math.pow(2, notes.get(i) / 12.0), 1-(i+j*notes.size())/n);
                au.generateFrames(750);
                instr.release();
                au.generateFrames(250);
            }
        }
    }
}
