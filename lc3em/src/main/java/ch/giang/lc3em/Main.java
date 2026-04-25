package ch.giang.lc3em;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) throws IOException, FileNotFoundException {
        if (args.length == 1) {
            // try to read file
            InputStream in = new FileInputStream(args[0]);
            Memory mem = new Memory(in);
            in.close();
            for (int i = 0; i < 9; i++) {
                System.out.println(mem.getBits((short) i));
            }
        }

        // writeTest();
        testInstruction();
        // readTest();

    }

    public static void testInstruction() {
        Instruction inst = new Instruction("0001000100100010");
        System.out.println(inst.op);
        assert inst.bit(12) && inst.bit(1) && inst.bit(5) && inst.bit(8);
    }

    public static void writeTest() throws IOException {
        OutputStream out = new FileOutputStream("a.obj");
        // out.write(new byte[] { (byte) 0xF0, 0x20, 0x14, (byte) 0x81 });
        out.write(new byte[] { 0x30, 0x00, (byte) 0xe0, 0x02, (byte) 0xf0, 0x22, (byte) 0xf0, 0x25, 0x00, 0x48, 0x00,
                0x65, 0x00, 0x79, 0x00, 0x0a, 0x00, 0x00 });
        out.close();

    }

    public static void readTest() throws IOException {
        byte[] data = new byte[] { 0x01, 0x00, (byte) 0xe0, 0x02, (byte) 0xf0, 0x22, (byte) 0xf0, 0x25, 0x00, 0x48,
                0x00, 0x65, 0x00, 0x79, 0x00, 0x0a };

        // byte[] data = new byte[] { (byte) 0xF0, 0x20, 0x14, (byte) 0x81, };
        // byte[] data = new byte[] { 0x20, (byte) 0x30,0x14, (byte) 0x81, };
        // should be:
        // 1111 0000 0010 0000 ;trap into 0x20 get char
        // 0001 0100 1000 0001 ;add R2, R2, R1
        InputStream si = new ByteArrayInputStream(data);
        Memory mem = new Memory(si);
        for (int i = 256; i < 270; i++) {
            System.out.println(mem.getBits((short) i));
        }

    }
}