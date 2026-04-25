package ch.giang.lc3em;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException, FileNotFoundException {
        // TODO: load memory from file specified in command
        testInstruction();
        // writeTest();
        readTest();

    }

    public static void testInstruction() {
        Instruction inst = new Instruction("0001000100100010");
        System.out.println(inst.op);
        assert inst.bit(12) && inst.bit(1) && inst.bit(5) && inst.bit(8);

    }

    public static void readTest() throws IOException, FileNotFoundException {
        byte[] data = new byte[] { (byte) 0xF0, 0x20, 0x14, (byte) 0x81, };
        // byte[] data = new byte[] {  0x20, (byte) 0x30,0x14, (byte) 0x81, };
        // should be:
        // 1111 0000 0010 0000 ;trap into 0x20 get char
        // 0001 0100 1000 0001 ;add R2, R2, R1
        InputStream si = new ByteArrayInputStream(data);
        Memory mem = new Memory(si);
        System.out.println(mem.getBits((short) 0x00));
        System.out.println(mem.getBits((short) 0x01));

    }
}