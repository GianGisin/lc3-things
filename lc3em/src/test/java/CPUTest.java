import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

    @Test
    public void testFlipBit(){
        Assert.assertEquals(32, CPU.flipBit(5,(short) 0));
        Assert.assertEquals(0, CPU.flipBit(5,(short) 32));
        Assert.assertEquals(-32768, CPU.flipBit(15,(short) 0));
        Assert.assertEquals(29612, CPU.flipBit(15,(short) 0xF3AC));
    }

    @Test
    public void testPSRCCcoherence(){
        CPU c = new CPU(new Memory(), (short)0);
        c.setConditionCodes((short)0);
        Assert.assertEquals(0, c.condition());
        Assert.assertEquals(true, CPU.getBit(1, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(2, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(0, c.getPSR()));

        c.setConditionCodes((short)-1);
        Assert.assertEquals(-1, c.condition());
        Assert.assertEquals(false, CPU.getBit(1, c.getPSR()));
        Assert.assertEquals(true, CPU.getBit(2, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(0, c.getPSR()));

        c.setConditionCodes((short)1);
        Assert.assertEquals(1, c.condition());
        Assert.assertEquals(false, CPU.getBit(1, c.getPSR()));
        Assert.assertEquals(false, CPU.getBit(2, c.getPSR()));
        Assert.assertEquals(true, CPU.getBit(0, c.getPSR()));


    }

    @Test
    public void testProgrammAdd() throws FileNotFoundException, IOException{
        InputStream in = new FileInputStream("src/test/resources/add.obj");
        Memory mem = new Memory(in);
        in.close();
        for(int i = 0; i < 10; i++){
            System.out.println(mem.getBits((short)(0x3000 + i)));
        }
        CPU cpu = new CPU(mem, (short) 0x3000);
        Assert.assertEquals(8708, mem.getShort((short)0x3000)); // fist instruction at correct location
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
        Assert.assertEquals(13, mem.getShort((short) (0x3000 + 7))); //store
        cpu.step();
        Assert.assertEquals(6, cpu.register(1));
    }

}
