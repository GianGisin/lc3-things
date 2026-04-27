import org.junit.Assert;
import org.junit.Test;

import ch.giang.lc3em.CPU;

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

}
