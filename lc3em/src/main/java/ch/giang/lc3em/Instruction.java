package ch.giang.lc3em;

public class Instruction {
    public final opCode op;
    private String bits;

    public Instruction(String bits) {
        assert bits.length() == 16;
        this.bits = bits;
        op = opCodeFromBits(bits.substring(0, 4));
    }

    // offset literals
    public int PCOffset9() {
        return CPU.sext(bits.substring(7));
    }

    public int PCOffset11() {
        return CPU.sext(bits.substring(5));
    }

    public int Offset6() {
        return CPU.sext(bits.substring(10));
    }

    public int Trapvect8() {
        return Integer.parseInt(bits.substring(8), 2);
    }

    // immediate for and + add
    public short Imm5() {
        return CPU.sext(bits.substring(11));
    }

    public short SR1() {
        return Short.parseShort(bits.substring(7, 10), 2);
    }

    public short SR2() {
        return Short.parseShort(bits.substring(13, 16), 2);
    }

    public short BaseR() {
        return SR1();
    }

    // destination register for operate instructions
    public short DR() {
        return Short.parseShort(bits.substring(4, 7), 2);
    }

    public short SR() {
        return DR();
    }

    public boolean immBit() {
        return bit(5);
    }

    // condition codes
    public boolean n() {
        return bit(11);
    }

    public boolean z() {
        return bit(10);
    }

    public boolean p() {
        return bit(9);
    }

    // returns true if offset mode, else base register mode
    public boolean jumpMode() {
        return bit(11);
    }

    public boolean bit(int index) {
        return bits.charAt(15 - index) == '1';
    }

    static opCode opCodeFromBits(String bits) {
        // FIXME: opcode.values takes O(n) because it copies the array
        return opCode.values()[Integer.parseInt(bits, 2)];
    }
}