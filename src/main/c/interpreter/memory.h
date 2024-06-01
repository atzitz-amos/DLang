//
// Created by amosa on 30.05.2024.
//

#ifndef DLANG_MEMORY_H
#define DLANG_MEMORY_H

#include <ctype.h>

#define MEMORY_SIZE 16777216

// MAIN MEMORY
static int *p_memory;

static int p_sp;
static int p_local;
static int p_param;

void DL_InitMemory();


void DL_MemorySet(int index, int value);

int DL_MemorySeek(int index);


void DL_LocalSet(int index, int value);

int DL_LocalGet(int index);


void DL_ParamSet(int index, int value);

int DL_ParamGet(int index);


void DL_StackPush(int value);

int DL_StackPop();


void DL_SetSP(int offset);

void DL_SetLocal(int offset);

void DL_SetParam(int offset);

int DL_GetpSP();

int DL_GetpLocal();

int DL_GetpParam();


#endif
