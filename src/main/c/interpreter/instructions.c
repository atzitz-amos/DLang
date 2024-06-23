//
// Created by amosa on 01.06.2024.
//

#include "instructions.h"

int p_pc = 0;

inline void instruct_pc_jmp(int value) {
    p_pc = value;
}

inline void instruct_pc_incr() {
    p_pc++;
}

inline int instruct_pc_get() {
    return p_pc;
}
