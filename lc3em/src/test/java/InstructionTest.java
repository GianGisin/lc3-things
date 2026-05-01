import org.junit.Test;
import org.junit.Assert;

import ch.giang.lc3em.Instruction;
import ch.giang.lc3em.opCode;

public class InstructionTest {
    @Test
    public void subInstructions() {
        Instruction inst2 = new Instruction("0000101011011101");
        Assert.assertEquals(inst2.DR(), 5);
        Assert.assertEquals(inst2.SR1(), 3);
        Assert.assertEquals(inst2.Imm5(), -3);
        Assert.assertEquals(inst2.SR2(), 5);
        Assert.assertEquals(inst2.n(), true);
        Assert.assertEquals(inst2.z(), false);
        Assert.assertEquals(inst2.p(), true);
        Assert.assertEquals(inst2.Trapvect8(), 221);
        System.out.println(inst2.Trapvect8());
    }

    @Test
    public void opCode() {
        Instruction ins = new Instruction("1000000000000000");
        Assert.assertEquals(ins.op, opCode.RTI);
    }

    @Test
    public void getBits(){
        Instruction inst = new Instruction("0001000100100010");
        Assert.assertTrue(inst.bit(12) && inst.bit(1) && inst.bit(5) && inst.bit(8));
    }

}