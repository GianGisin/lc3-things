package ch.giang.lc3em;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException, FileNotFoundException {
        if (args.length == 1) {
            // try to read file
            InputStream in = new FileInputStream(args[0]);
            Memory mem = new Memory(in);
            in.close();
            // FIXME: where does the initial PC come from?
            CPU cpu = new CPU(mem, (short) 0x3000);
            cpu.step();
        }

    }

}