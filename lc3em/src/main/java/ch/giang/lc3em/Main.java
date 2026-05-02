package ch.giang.lc3em;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, FileNotFoundException {
        if (args.length == 1) {
            // try to read file
            InputStream in = new FileInputStream(args[0]);
            Memory mem = new Memory(in);
            in.close();
            // FIXME: where does the initial PC come from?
            CPU cpu = new CPU(mem, (short) 0x3000);
            Scanner s = new Scanner(System.in);
            while (true) {
                String l = s.nextLine();
                if (l.startsWith("q"))
                    break;
                cpu.step();
                System.out.println(mem.repr((short) 0x3000, (short) 0x300A));
                System.out.println(cpu.regsToString());
            }
            s.close();
        }
    }
}