import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import ch.giang.lc3em.CPU;
import ch.giang.lc3em.Memory;

public class CPUTest {
    // ---- Helper methods ----
    private Memory memFactory(short startIndex, String[] content) {
        byte[] data = new byte[(content.length + 1) * 2]; // every instruction is two bytes
        data[0] = (byte) (startIndex >> 8);
        data[1] = (byte) (startIndex & (short) 0x00FF);

        // read content into data[]
        int contIndex = 0;
        String s = "";
        for (int i = 2; i < (content.length + 1) * 2; i++) {
            if (i % 2 == 0) {
                s = content[contIndex++];
                data[i] = (byte) Short.parseShort(s.substring(0, 8), 2);
            } else {
                data[i] = (byte) Short.parseShort(s.substring(8), 2);
            }
        }

        InputStream in = new ByteArrayInputStream(data);
        Memory mem;
        try {
            mem = new Memory(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mem;
    }

    // ---- Tests ----
    @Test
    public void testSext() {
        Assert.assertEquals(31, CPU.sext("011111"));
        Assert.assertEquals(-1, CPU.sext("11"));
        Assert.assertEquals(-16, CPU.sext("10000"));
        Assert.assertEquals(-12, CPU.sext("10100"));
    }

    @Test
    public void testGetBit() {
        short test = 1;
        for (int i = 0; i < 16; i++) {
            Assert.assertTrue(CPU.getBit(i, test));
            test *= 2;
        }
    }

    @Test
    public void testAccessControl() {
        CPU c = new CPU(new Memory(), (short) 0x3000);
        Assert.assertEquals(false, c.checkAccess((short) 0x0000));
        Assert.assertEquals(false, c.checkAccess((short) 0x01FF));
        Assert.assertEquals(false, c.checkAccess((short) 0x2FFF));

        Assert.assertEquals(true, c.checkAccess((short) 0x3000));
        Assert.assertEquals(true, c.checkAccess((short) 0xFDFF)); // -513

        Assert.assertEquals(false, c.checkAccess((short) 0xFE00));
        Assert.assertEquals(false, c.checkAccess((short) 0xFEF3));
        Assert.assertEquals(false, c.checkAccess((short) 0xFFFF)); // -1
    }

    @Test
    public void testFlipBit() {
        Assert.assertEquals(32, CPU.flipBit(5, (short) 0));
        Assert.assertEquals(0, CPU.flipBit(5, (short) 32));
        Assert.assertEquals(-32768, CPU.flipBit(15, (short) 0));
        Assert.assertEquals(29612, CPU.flipBit(15, (short) 0xF3AC));
    }

    @Test
    public void testPSRCCcoherence() {
        CPU c = new CPU(new Memory(), (short) 0);
        c.setConditionCodes((short) 0);
        Assert.assertEquals(0, c.condition());
        Assert.assertEquals(true, CPU.getBit(1, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(2, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(0, c.getPSR()));

        c.setConditionCodes((short) -1);
        Assert.assertEquals(-1, c.condition());
        Assert.assertEquals(false, CPU.getBit(1, c.getPSR()));
        Assert.assertEquals(true, CPU.getBit(2, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(0, c.getPSR()));

        c.setConditionCodes((short) 1);
        Assert.assertEquals(1, c.condition());
        Assert.assertEquals(false, CPU.getBit(1, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(2, c.getPSR()));
        Assert.assertEquals(true, CPU.getBit(0, c.getPSR()));

    }

    @Test
    public void testProgrammAdd() throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream("src/test/resources/add.obj");
        Memory mem = new Memory(in);
        in.close();
        CPU cpu = new CPU(mem, (short) 0x3000);
        Assert.assertEquals(8708, mem.getShort((short) 0x3000)); // fist instruction at correct location
        cpu.step();
        Assert.assertEquals(6, cpu.register(1)); // first load
        Assert.assertEquals(1, cpu.condition());
        cpu.step();
        Assert.assertEquals(7, cpu.register(2)); // second load
        Assert.assertEquals(1, cpu.condition());
        cpu.step();
        Assert.assertEquals(13, cpu.register(3)); // add
        Assert.assertEquals(1, cpu.condition());
        cpu.step();
        Assert.assertEquals(13, mem.getShort((short) (0x3000 + 7))); // store
        cpu.step();
        Assert.assertEquals(6, cpu.register(1));
    }

    // all operations that set flags
    @Test
    public void testAdd() {
        Memory mem = memFactory((short) 0x3000, new String[] {
                "0001101000000001", // add r5,r0,r1
                "0001101101111110", // add r5, r5, -2
                "0001101101111110" // add r5, r5, -2
        });
        CPU c = new CPU(mem, (short) 0x3000);
        c.setRegister(0, (short) 2);
        c.step();
        Assert.assertEquals(c.register(5), (short) 2);
        Assert.assertEquals(c.condition(), 1);
        c.step();
        Assert.assertEquals(c.register(5), (short) 0);
        Assert.assertEquals(c.condition(), 0);
        c.step();
        Assert.assertEquals(c.register(5), (short) -2);
        Assert.assertEquals(c.condition(), -1);

    }

    @Test
    public void testAnd() {
        Memory mem = memFactory((short) 0x3000, new String[] {
                "0101101000000001", // and r5,r0,r1
                "0101101101111111", // and r5, r5, -1
        });
        CPU c = new CPU(mem, (short) 0x3000);
        c.setRegister(0, (short) 10025);
        c.setRegister(1, (short) 45628);
        c.step();
        Assert.assertEquals(c.register(5), (short) 8744);
        Assert.assertEquals(c.condition(), 1);
        c.step();
        Assert.assertEquals(c.register(5), (short) 8744);
        Assert.assertEquals(c.condition(), 1);

    }

    @Test
    public void testLd() {
        Memory mem = memFactory((short) 0x3000, new String[] {
                "0010011000000010", // LD R3, +2
                "1100000111000000", // JMP R7
                "0000000000000000",
                "0000000000101010",
                "1111111111111011",
                "0000000000000000",
                "0010011111111101", // LD R3, -3
        });
        CPU c = new CPU(mem, (short) 0x3000);
        c.setRegister(7, (short) 0x3006);
        c.step();
        Assert.assertEquals(c.register(3), (short) 42);
        Assert.assertEquals(c.condition(), 1);
        c.step();
        Assert.assertEquals(c.getPC(), 0x3006);
        c.step();
        Assert.assertEquals(c.register(3), (short) -5);
        Assert.assertEquals(c.condition(), -1);
    }

    @Test
    public void testLdi() {
        Memory mem = memFactory((short) 0x3000, new String[] {
                "1010110000000010", // LDI R6, 2
                "0000000000000000",
                "0000000000000000",
                "0011000000000100", // address 0x3004
                "1111111111111011", // data
        });
        CPU c = new CPU(mem, (short) 0x3000);
        c.step();
        Assert.assertEquals(c.register(6), (short) -5);
        Assert.assertEquals(c.condition(), -1);

    }

    @Test
    public void testLdr() {
        Memory mem = memFactory((short) 0x3000, new String[] {
                "0110010011000100", // LDR R2, R3, 4
                "0000000000000000",
                "0000000000000000",
                "0000000000000000",
                "1111111111111011", // data
        });
        CPU c = new CPU(mem, (short) 0x3000);
        c.setRegister(3, (short) 0x3000);
        c.step();
        Assert.assertEquals(c.register(2), (short) -5);
        Assert.assertEquals(c.condition(), -1);

    }

    @Test
    public void testNot() {
        Memory mem = memFactory((short) 0x3000, new String[] {
                "1001011010111111", // NOT R3, R2
        });
        CPU c = new CPU(mem, (short) 0x3000);
        c.setRegister(2, (short) 10025);
        c.step();
        Assert.assertEquals(c.register(3), (short) -10026);
        Assert.assertEquals(c.condition(), -1);

    }

}
