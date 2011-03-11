package qbg.jtrack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Store heterogeneous collection of primitive settings
 */
public class SettingsBuffer {
    /**
     * List of generated shards
     */
    private List<byte[]> shards = new ArrayList<byte[]>();
    /**
     * The length of the result
     */
    private int length = 0;
    /**
     * The byte array we modify
     */
    private byte[] backing = new byte[8];
    /**
     * The ByteBuffer we use
     */
    private ByteBuffer bb = ByteBuffer.wrap(backing);

    /**
     * Default constructor
     */
    public SettingsBuffer() {
        bb.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Store cnt bytes into the storage
     * @param cnt
     */
    private void putContents(int cnt) {
        byte[] ptr = (length == 0) ? null : shards.get(shards.size()-1);
        
        for (int i = 0; i < cnt; i++) {
            if ((length & 1023) == 0) {
                shards.add(ptr = new byte[1024]);
            }
            ptr[length & 1023] = backing[i];
            length++;
        }
    }

    /**
     * Load cnt bytes from the storage
     * @param cnt
     */
    private void loadContents(int cnt) {
        byte[] ptr = shards.get(length >> 10);
        
        for (int i = 0; i < cnt; i++) {
            if ((length & 1023) == 0) {
                ptr = shards.get(length >> 10);
            }
            backing[i] = ptr[length & 1023];
            length++;
        }
    }

    /**
     * Store an int
     * @param i
     */
    public void putInt(int i) {
        bb.putInt(0, i);
        putContents(4);
    }

    /**
     * Store a double
     * @param d
     */
    public void putDouble(double d) {
        bb.putDouble(0, d);
        putContents(8);
    }

    /**
     * Store a boolean
     * @param b
     */
    public void putBoolean(boolean b) {
        bb.put(0, (byte)(b ? 1 : 0));
        putContents(1);
    }

    /**
     * Load an int
     * @return The value
     */
    public int getInt() {
        loadContents(4);
        return bb.getInt(0);
    }

    /**
     * Load a double
     * @return The value
     */
    public double getDouble() {
        loadContents(8);
        return bb.getDouble(0);
    }

    /**
     * Load a boolean
     * @return The value
     */
    public boolean getBoolean() {
        loadContents(1);
        return bb.get(0) == 1;
    }
}
