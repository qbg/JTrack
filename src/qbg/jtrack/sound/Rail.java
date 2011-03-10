package qbg.jtrack.sound;

/**
 * Chaining together several sound operations
 */
public class Rail implements SoundOperation {
    /**
     * Array of operations to perform in order
     */
    private final SoundOperation[] operations;
    
    /**
     * Construct a new Rail that performs operations in order
     * @param operations
     */
    public Rail(SoundOperation... operations) {
        this.operations = operations;
    }
    
    @Override
    public void fillBuffer(int[] buffer) {
        for (int i = 0; i < operations.length; i++) {
            operations[i].fillBuffer(buffer);
        }
    }

}
