package ch.giang.lc3em;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Memory {
    private final static Logger log = Logger.getLogger(Memory.class.getName());

    public final int MEM_SIZE = 65_536; // 2^16 possible addresses
    private short[] memArray; // short is a 16 bit two's complement integer

    public Memory() {
        log.setLevel(Level.WARNING);
        memArray = new short[MEM_SIZE];
    }

    // read
    public Memory(InputStream in) throws IOException {
        this();
        byte[] data = in.readAllBytes();
        log.info(data.length + " bytes read from input file");
        String s1 = String.format("%8s", Integer.toBinaryString(data[0] & 0xFF)).replace(' ', '0');
        String s2 = String.format("%8s", Integer.toBinaryString(data[1] & 0xFF)).replace(' ', '0');
        int start = Integer.parseInt(s1+s2, 2); 
        // FIXME: proper handling
        assert data.length - start <= 2 * MEM_SIZE;
        String prev = "";
        for (int i = 2; i < data.length; i++) {
            int j = i - 2;
            log.info(data[i] + "");
            if (i % 2 == 1) {
                String curr = String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
                memArray[j / 2 + start] = (short) Integer.parseInt(prev + curr, 2);
            } else {
                prev = String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
            }

        }
        in.close();

    }

    public boolean set(short address, short value) {
        // TODO: check privilege level
        memArray[Short.toUnsignedInt(address)] = value;
        return true;
    }

    public short getShort(short address) {
        // TODO: handle access to memor mapped IO
        // TODO: check that this is consistent with memory offset calculations
        int abs_address = Short.toUnsignedInt(address);
        if(abs_address >= 0xFE00) throw new NotImplementedException("Memory-mapped IO is not yet implemented");
        return memArray[abs_address];
    }

    public Instruction getInstruction(short address) {
        return new Instruction(getBits(address));
    }

    public String getBits(short address) {
        // FIXME: this might be obsolete if we can get singular bit values directly from
        // short
        return shortToBitstring(getShort(address));
    }

    private static String shortToBitstring(short word) {
        String bits = Integer.toBinaryString(Short.toUnsignedInt(word));
        return String.format("%16s", bits).replace(' ', '0');

    }

}
