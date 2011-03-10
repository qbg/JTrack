package qbg.jtrack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SettingsBuffer {
    private List<byte[]> shards = new ArrayList<byte[]>();
    private int length = 0;
    private byte[] backing = new byte[8];
    private ByteBuffer bb = ByteBuffer.wrap(backing);
    
    public SettingsBuffer() {
        bb.order(ByteOrder.LITTLE_ENDIAN);
    }
    
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
    
    public void putInt(int i) {
        bb.putInt(0, i);
        putContents(4);
    }
    
    public void putDouble(double d) {
        bb.putDouble(0, d);
        putContents(8);
    }
    
    public void putBoolean(boolean b) {
        bb.put(0, (byte)(b ? 1 : 0));
        putContents(1);
    }
    
    public int getInt() {
        loadContents(4);
        return bb.getInt(0);
    }
    
    public double getDouble() {
        loadContents(8);
        return bb.getDouble(0);
    }
    
    public boolean getBoolean() {
        loadContents(1);
        return bb.get(0) == 1;
    }
}
