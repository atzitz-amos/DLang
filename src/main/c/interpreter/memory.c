//
// Created by amosa on 30.05.2024.
//

#include <malloc.h>
#include "memory.h"

void DL_InitMemory() {
    p_memory = malloc(MEMORY_SIZE * sizeof(int));

    p_sp = 0;
    p_local = 0;
    p_param = 0;
}

void DL_MemorySet(int index, int value) {
    p_memory[index] = value;
}

int DL_MemorySeek(int index) {
    return p_memory[index];
}

void DL_LocalSet(int index, int value) {
    p_memory[p_local + index] = value;
}

int DL_LocalGet(int index) {
    return p_memory[p_local + index];
}

void DL_ParamSet(int index, int value) {
    p_memory[p_param + index] = value;
}

int DL_ParamGet(int index) {
    return p_memory[p_param + index];
}

void DL_StackPush(int value) {
    p_memory[p_sp++] = value;
}


int DL_StackPop() {
    return p_memory[--p_sp];
}

void DL_SetSP(int offset) {
    p_sp = offset;
}

void DL_SetLocal(int offset) {
    p_local = offset;
}

void DL_SetParam(int offset) {
    p_param = offset;
}

int DL_GetpSP() {
    return p_sp;
}

int DL_GetpLocal() {
    return p_local;
}


int DL_GetpParam() {
    return p_param;
}
