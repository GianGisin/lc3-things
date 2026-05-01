orig x3000

	LD R1, S1
	LD R2, S2
	ADD R3, R1, R2
	ST R3, RESULT
	halt


S1 .fill 0x0006
S2 .fill 0x0007
RESULT .blkw 1
.end