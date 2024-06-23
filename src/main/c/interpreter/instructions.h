//
// Created by amosa on 01.06.2024.
//

#ifndef DLANG_INSTRUCTIONS_H
#define DLANG_INSTRUCTIONS_H

#include "opcode.h"
#include <stdio.h>

// INSTRUCTIONS MEMORY
struct instruction_memory {
    size_t size;
    opcode_t *ops;
};

extern int p_pc;

void instruct_pc_jmp(int value);

void instruct_pc_incr();

int instruct_pc_get();

#endif //DLANG_INSTRUCTIONS_H
