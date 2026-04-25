import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.giang.lc3em.Instruction;
import ch.giang.lc3em.opCode;

public class InstructionTest {
    @Test
    void subInstructions() {
        Instruction inst2 = new Instruction("0000101011011101");
        assertEquals(inst2.DR(), 5);
        assertEquals(inst2.SR1(), 3);
        assertEquals( inst2.Imm5(), 29);
        assertEquals( inst2.SR2(), 5);
        assertEquals( inst2.n(), true);
        assertEquals( inst2.z(), false);
        assertEquals( inst2.p(), true);
        assertEquals( inst2.Trapvect8(), 22);
        System.out.println(inst2.Trapvect8());
    }
    @Test
    void opCode() {
        Instruction ins = new Instruction("1000000000000000");
        assertEquals(ins.op, opCode.ADD);
    }

}
