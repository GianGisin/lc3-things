import org.junit.Assert;
import org.junit.Test;

import ch.giang.lc3em.CPU;
import ch.giang.lc3em.Memory;

public class CPUTest {
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

    // TODO: test flipBit
    @Test
    public void testFlipBit(){
        Assert.assertEquals(32, CPU.flipBit(5,(short) 0));
        Assert.assertEquals(0, CPU.flipBit(5,(short) 32));
        Assert.assertEquals(-32768, CPU.flipBit(15,(short) 0));
        Assert.assertEquals(29612, CPU.flipBit(15,(short) 0xF3AC));
    }

}
