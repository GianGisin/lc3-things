package ch.giang.lc3em;

public class CPU {
    private short[] RF; // register file
    private short PC; // program counter
    private short MCR; // machine control register (clock enable)
    private short PSR; // processor status register
    private short saved_ssp; // saved stack pointers
    private short saved_usp;
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
        MCR = (short) 0x8000; // enable clock
        PSR = (short) 0x8000; // set processor to user mode, priority 0
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

    public String regsToString() {
        String res = "PC: " + PC + " ";
        for (int i = 0; i < RF.length; i++) {
            res += "R" + i + ":" + RF[i] + " ";
        }
        res += "\nusp: " + saved_usp + " ssp: " + saved_ssp + " NZP: " + (N ? "N":(Z? "Z":"P"));
        return res;
    }

    public short register(int index){
        return RF[index];
    }

    public int condition(){
        return P? 1: (N? -1:0);
    }

    public short getPSR(){
        return PSR;
    }

    public void setConditionCodes(short s) {
        Z = P = N = false;
        
        // clear the condition codes from PSR in the worst possible way
        PSR >>= 3; 
        PSR <<= 3;

        if (s < 0) {
            N = true;
            PSR = flipBit(2, PSR);
        } else if (s > 0) {
            P = true;
            PSR = flipBit(0, PSR);
        } else {
            Z = true;
            PSR = flipBit(1, PSR);
        }
    }

    public void initiateException(LC3Exception e) {
        short temp = PSR;
        if (getBit(15, PSR)) { // process causing exception was in user mode
            PSR = flipBit(15, PSR);
            saved_usp = RF[6];
            RF[6] = saved_ssp;
        }
        // push temp and pc on system stack
        RF[6]--;
        mem.set(RF[6], temp);
        RF[6]--;
        mem.set(RF[6], PC);
        short exCode = (short) e.ordinal();
        short tableAddr = (short) (exCode + 0x0100);
        // 0x0100 priv mode
        // 0x0101 illegal opCode
        // 0x0102 access control violation
        PC = mem.getShort(tableAddr);
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
        if (index >= 16 || index < 0)
            throw new IllegalArgumentException();
        return ((value >>> index) & 1) == 0 ? false : true;
    }

    public static short flipBit(int index, short value) {
        if (index >= 16 || index < 0)
            throw new IllegalArgumentException();
        short power = (short) Math.pow(2, index);
        if (getBit(index, value)) {
            return (short) (value - power);
        } else {
            return (short) (value + power);
        }
    }

    public boolean checkAccess(short address) {
        if (getBit(15, PSR)) {
            // we are in user mode, check if the address is in kernel memory
            // (system space) (I/O Page)
            if ((address >= 0 && address < 0x3000) || (address <= -1 && address >= -512))
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
        short offset6 = inst.Offset6();
        short BaseR = inst.BaseR();

        System.out.println("Processing: " + inst.op);
        switch (inst.op) {
            case opCode.BR:
                if ((inst.n() && N) || (inst.z() && Z) || (inst.p() && P)) {
                    PC += pcoffset9;
                }
                break;

            case opCode.ADD:
                short sum;
                if (inst.immBit()) {
                    sum = (short) (RF[s1] + imm5);
                } else {
                    sum = (short) (RF[s1] + RF[s2]);
                }
                setConditionCodes(sum);
                RF[DR] = sum;
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

            case opCode.NOT:
                RF[DR] = (short) ~RF[s1];
                setConditionCodes(RF[DR]);
                break;

            case opCode.LD:
                // check for access violation
                short newaddr = (short) (PC + pcoffset9);
                if (checkAccess(newaddr)) {
                    RF[DR] = mem.getShort(newaddr);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    initiateException(LC3Exception.AccesControlViolation);
                }
                break;

            case opCode.ST:
                // check for access violation
                newaddr = (short) (PC + pcoffset9);
                if (checkAccess(newaddr)) {
                    mem.set(newaddr, RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    initiateException(LC3Exception.AccesControlViolation);
                }
                break;

            case opCode.LDI:
                short pointer = (short) (PC + pcoffset9);
                newaddr = mem.getShort(pointer);
                if (checkAccess(newaddr) && checkAccess(pointer)) {
                    RF[DR] = mem.getShort(newaddr);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    initiateException(LC3Exception.AccesControlViolation);
                }
                break;

            case opCode.STI:
                pointer = (short) (PC + pcoffset9);
                newaddr = mem.getShort(pointer);
                if (checkAccess(newaddr) && checkAccess(pointer)) {
                    mem.set(newaddr, RF[DR]);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    initiateException(LC3Exception.AccesControlViolation);
                }
                break;

            case opCode.LDR:
                newaddr = (short) (RF[BaseR] + offset6);
                if (checkAccess(newaddr)) {
                    RF[DR] = mem.getShort(newaddr);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    initiateException(LC3Exception.AccesControlViolation);
                }
                break;

            case opCode.STR:
                newaddr = (short) (RF[BaseR] + offset6);
                if (checkAccess(newaddr)) {
                    mem.set(newaddr, RF[DR]);
                    setConditionCodes(RF[DR]);
                } else {
                    System.out.println("Access Control Violation at PC " + PC);
                    initiateException(LC3Exception.AccesControlViolation);
                }
                break;

            case opCode.LEA:
                RF[DR] = (short) (PC + pcoffset9);
                break;

            case opCode.JSR:
                boolean offsetMode = inst.jumpMode();
                short temp = PC;
                if (offsetMode) {
                    PC += inst.PCOffset11();
                } else {
                    PC = RF[BaseR];
                }
                RF[7] = temp;
                break;

            case opCode.RTI:
                if (getBit(15, PSR)) {
                    initiateException(LC3Exception.PrivilegeModeViolation);
                } else {
                    PC = mem.getShort(RF[6]); // load PC from system stack
                    RF[6]++;
                    temp = mem.getShort(RF[6]);
                    RF[6]++;
                    PSR = temp;
                    if (getBit(15, PSR)) { // dropping back to user mode? switch to user stack.
                        saved_ssp = RF[6];
                        RF[6] = saved_usp;
                    }
                }
                break;

            case opCode.TRAP:
                temp = PSR;
                if (getBit(15, PSR)) {
                    saved_usp = RF[6];
                    RF[6] = saved_ssp;
                    PSR = flipBit(15, PSR);
                }
                // push temp, pc on sys stack
                RF[6]--;
                mem.set(RF[6], temp);
                RF[6]--;
                mem.set(RF[6], PC);

                PC = mem.getShort(inst.Trapvect8());
                break;

            case opCode.JMP:
                PC = BaseR;
                break;

            case opCode.ILLEGAL:
                initiateException(LC3Exception.IllegalOpcodeException);
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
