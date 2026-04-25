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
        // FIXME: proper handling
        assert data.length <= 2 * MEM_SIZE;
        String prev = "";
        for (int i = 0; i < data.length; i++) {
            log.info(data[i] + "");
            if (i % 2 == 1) {
                String curr = String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
                memArray[i / 2] = (short) Integer.parseInt(prev + curr, 2);
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
        // TODO: check that this is consistent with memory offset calculations
        return memArray[Short.toUnsignedInt(address)];
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
