//
// Created by amosa on 31.05.2024.
//

#ifndef DLANG_HEAP_H
#define DLANG_HEAP_H

#include <ctype.h>

#define HEAP_SIZE 67108864

typedef struct buddy_allocator_pair {
    int lb;
    int ub;
} buddy_pair_t;

typedef struct buddy_bucket {
    size_t size;
    size_t capacity;
    buddy_pair_t *arr;
} buddy_bucket_t;


static int *heap;
static buddy_bucket_t *allocator_buckets;

static int p_this;

void DL_InitHeap();
void DL_HeapFree();

void DL_SetpThis(int value);

int DL_GetpThis();

void DL_HeapSetAbs(int index, int value);

int DL_HeapGet(int index);

void DL_HeapSetRel(int offset, int value);

int DL_HeapGetRel(int offset);

int DL_HeapAlloc(int size);

int DL_HeapAllocAndPosition(int size);


#endif //DLANG_HEAP_H
