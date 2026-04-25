package ch.giang.lc3em;

public class Instruction {
    public opCode op;
    private String bits;

    public Instruction(String bits) {
        assert bits.length() == 16;
        this.bits = bits;
        op = opCodeFromBits(bits.substring(0, 4));
    }

    public int PCOffset9() {
        return Integer.parseInt(bits.substring(7), 2);
    }

    public int PCOffset11() {
        return Integer.parseInt(bits.substring(5), 2);
    }

    public int Offset6() {
        return Integer.parseInt(bits.substring(10), 2);
    }

    public int Trapvect8() {
        return Integer.parseInt(bits.substring(8), 2);
    }

    public short Imm5() {
        return Short.parseShort(bits.substring(11), 2);
    }

    public short SR1() {
        return Short.parseShort(bits.substring(7, 10), 2);
    }

    public short BaseR() {
        return SR1();
    }

    public short SR2() {
        return Short.parseShort(bits.substring(13, 16), 2);
    }

    public short DR() {
        return Short.parseShort(bits.substring(4, 7));
    }

    public short SR() {
        return DR();
    }

    public boolean immBit() {
        return bit(5);
    }

    public boolean n() {
        return bit(11);
    }

    public boolean z() {
        return bit(10);
    }

    public boolean p() {
        return bit(9);
    }

    public boolean bit(int index) {
        return bits.charAt(15 - index) == '1';
    }

    static opCode opCodeFromBits(String bits) {
        // FIXME: opcode.values takes O(n) because it copies the array
        return opCode.values()[Integer.parseInt(bits, 2)];
    }

}

enum opCode {
    BR, // 0000 0
    ADD, // 0001 1
    LD, // 0010 2
    ST, // 0011 3
    JSR, // 0100 4
    AND, // 0101 5
    LDR, // 0110 6
    STR, // 0111 7
    RTI, // 1000 8
    NOT, // 1001 9
    LDI, // 1010 10
    STI, // 1011 11
    RET, // 1100 12
    ILLEGAL, // 1101 13
    LEA, // 1110 14
    TRAP, // 1111 15

}