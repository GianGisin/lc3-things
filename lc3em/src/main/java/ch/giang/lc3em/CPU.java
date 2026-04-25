package ch.giang.lc3em;

public class CPU {
    private short[] RF; // register file
    private int PC; // program counter
    private short MCR; // machine control register (clock enable)
    private short PSR; // processor status register
    private boolean N;
    private boolean Z;
    private boolean P;
    Memory mem;

    public CPU(Memory mem, int PCInit) {
        this.mem = mem;
        PC = PCInit;
        RF = new short[8];
        MCR = 0x80; // enable clock
        PSR = 0x80; // set processor to user mode, priority 0
        N = Z = P = false;

    }

}
