# lc3 things
lc3 (Little Computer 3) is an instruction set architecture as specified by Patt and Patel in "introduction to computing systems" (third edition). This repository contains an emulator and an assembler for the LC3 ISA.

## lc3asm
My attempt at an lc3 assembler

## lc3em
An emulator for an LC3 compliant CPU. I try to follow the specifications as closely as possible. Some things lack clarification in the book (at least as far as i can tell, mostly from appendix A) so i just guessed lmao. (e.g. the condition codes are described at some points as "single-bit special purpose registers", but they are located in the PSR, so my implementation has every condition code as a separate register and the PSR is synchronized with those registers. This is of course not actually relevant for the corectness of the emulator)

