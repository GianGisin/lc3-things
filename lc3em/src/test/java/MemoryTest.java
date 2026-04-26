import org.junit.Test;

import ch.giang.lc3em.Memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;

public class MemoryTest {
    // helper functions
    static Memory memFrom(byte[] data) throws IOException {
        InputStream si = new ByteArrayInputStream(data);
        Memory mem = new Memory(si);
        return mem;

    }

    @Test
    public void memOffset() throws IOException {
        byte[] data = new byte[] { 0x01, 0x00, (byte) 0x80, (byte) 0x80 };
        Memory mem = memFrom(data);
        Assert.assertEquals("1000000010000000", mem.getBits((short) 0x0100));
    }

    @Test
    public void memReadIn() throws IOException {
        byte[] data = new byte[] { 0x00, 0x00, (byte) 0xe0, 0x02, (byte) 0xf0, 0x22, (byte) 0xf0, 0x25, 0x00, 0x48,
                0x00, 0x65, 0x00, 0x79, 0x00, 0x0a, 0x00, 0x00 };
        Memory mem = memFrom(data);
        String[] expected = new String[] {
                "1110000000000010",
                "1111000000100010",
                "1111000000100101",
                "0000000001001000",
                "0000000001100101",
                "0000000001111001",
                "0000000000001010",
                "0000000000000000"
        };

        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], mem.getBits((short) i));

        }
    }

    @Test
    public void shortCalc() throws IOException {
        Memory mem = memFrom(new byte[] { (byte) 0xF0, 0x00, 0x00, 0x01, 0x00, 0x02 });
        Assert.assertEquals(2, mem.getShort((short) (0xF000 + 0x0001)));
        Assert.assertEquals(1, mem.getShort((short) (0xF001 - 0x0001)));
    }

}