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

    public CPU(Memory mem, short PCInit, long clockPeriod) {
        this(mem, PCInit);
        this.clockPeriod = clockPeriod;
        throttleClock = true;
    }

    public void setClockPeriod(long value) {
        clockPeriod = value;
        throttleClock = clockPeriod <= 0 ? false : true;
    }

    private void setConditionCodes(short s) {
        Z = P = N = false;
        if (s < 0) {
            N = true;
        } else if (s > 0) {
            P = true;
        } else {
            Z = true;
        }
    }

    /***
     * Sign extends given bitstring and returns short
     * 
     * @param s bitstring with length at most 15
     * @return short containing extended value
     */
    public static short sext(String s) {
        char extWith;
        if (s.charAt(0) == '1') {
            // sext with 1s
            extWith = '1';
        } else {
            // sext with 0s
            extWith = '0';
        }
        String extended = String.format("%16s", s).replace(' ', extWith);
        return (short) Integer.parseInt(extended, 2);
    }

    public static boolean getBit(int index, short value) {
        return ((value >>> index) & 1) == 0 ? false : true;
    }

    public boolean checkAccess(short address) {
        if (getBit(15, PSR)) {
            // we are in user mode, check if the address is in kernel memory
            // (system space) (I/O Page)
            if (address < 0x3000 || (address <= -1 && address >= -512))
                return false;
        }
        return true;
    }

    public void step() {
        // FETCH
        Instruction inst = mem.getInstruction(PC);
        PC++;
        // DECODE
        short s1 = inst.SR1();
        short s2 = inst.SR2();
        short DR = inst.DR();
        short imm5 = inst.Imm5();
        short pcoffset9 = inst.PCOffset9();
        short BaseR = inst.BaseR();

        switch (inst.op) {
            case opCode.BR:
                if ((inst.n() && N) || (inst.z() && Z) || (inst.p() && P)) {
                    PC += pcoffset9;
                }
                break;

            case opCode.ADD:
                short sum;
                if (inst.immBit()) {
                    sum = (short) (s1 + imm5);
                } else {
                    sum = (short) (s1 + s2);
                }
                setConditionCodes(sum);
                RF[DR] = sum;
                break;

            case opCode.LD:
                // check for access violation
                short newaddr = (short) (PC + pcoffset9);
                if (checkAccess(newaddr)) {
                    RF[DR] = mem.getShort(newaddr);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    // TODO: Initiate ACV exception
                }
                break;

            case opCode.ST:
                // check for access violation
                newaddr = (short) (PC + pcoffset9);
                if (checkAccess(newaddr)) {
                    mem.set(newaddr, RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    // TODO: Initiate ACV exception
                }
                break;

            case opCode.JSR:
                break;
            case opCode.AND:
                short res;
                if (inst.immBit()) {
                    res = (short) (s1 & imm5);
                } else {
                    res = (short) (s1 & s2);
                }
                setConditionCodes(res);
                RF[DR] = res;
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
                short pointer = (short) (PC + pcoffset9);
                newaddr = mem.getShort(pointer);
                if (checkAccess(newaddr) && checkAccess(pointer)) {
                    RF[DR] = mem.getShort(newaddr);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    // TODO: Initiate ACV exception
                }
                break;
            case opCode.STI:
                break;
            case opCode.JMP:
                PC = BaseR;
                break;
            case opCode.ILLEGAL:
                break;
            case opCode.LEA:
                break;
            case opCode.TRAP:
                break;

        }

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
