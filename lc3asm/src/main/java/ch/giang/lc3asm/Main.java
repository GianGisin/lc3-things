package ch.giang.lc3asm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        writeTest();
    }
    public static void writeTest() throws IOException {
        OutputStream out = new FileOutputStream("bin_out");
        out.write(new byte[] { 0x30, 0x00, (byte) 0xe0, 0x02, (byte) 0xf0, 0x22, (byte) 0xf0, 0x25, 0x00, 0x48, 0x00,
                0x65, 0x00, 0x79, 0x00, 0x0a, 0x00, 0x00 });
        out.close();

    }
}