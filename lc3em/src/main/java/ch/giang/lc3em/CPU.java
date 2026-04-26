package ch.giang.lc3em;

public class CPU {
    private short[] RF; // register file
    private short PC; // program counter
    private short MCR; // machine control register (clock enable)
    private short PSR; // processor status register
    private boolean N; // condition codes
    private boolean Z;
    private boolean P;
    Memory mem;

    // aditional stuff
    private long clockPeriod;
    private boolean throttleClock;

    public CPU(Memory mem, short PCInit) {
        this.mem = mem;
        PC = PCInit;
        RF = new short[8];
        MCR = 0x80; // enable clock
        PSR = 0x80; // set processor to user mode, priority 0
        N = Z = P = false;

    }

    public void setClockPeriod(long value) {
        clockPeriod = value;
        throttleClock = clockPeriod <= 0 ? false : true;
    }

    public CPU(Memory mem, short PCInit, long clockPeriod) {
        this(mem, PCInit);
        this.clockPeriod = clockPeriod;
        throttleClock = true;
    }

    public void step() {
        // FETCH
        Instruction inst = mem.getInstruction(PC);
        // DECODE
        switch (inst.op) {

            case opCode.BR:
                break;
            case opCode.ADD:
                break;
            case opCode.LD:
                break;
            case opCode.ST:
                break;
            case opCode.JSR:
                break;
            case opCode.AND:
                break;
            case opCode.LDR:
                break;
            case opCode.STR:
                break;
            case opCode.RTI:
                break;
            case opCode.NOT:
                break;
            case opCode.LDI:
                break;
            case opCode.STI:
                break;
            case opCode.RET:
                break;
            case opCode.ILLEGAL:
                break;
            case opCode.LEA:
                break;
            case opCode.TRAP:
                break;

        }
        // EVALUATE ADRESS
        // FETCH OPERANDS
        // EXECUTE
        // WRITEBACK

    }

    public void run() {
        while (MCR != 0) { // loop until clock enable is reset
            step();
            // wait if throttling clock is enabled
            if (throttleClock) {
                try {
                    Thread.sleep(clockPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
