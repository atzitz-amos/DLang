//
// Created by amosa on 31.05.2024.
//

#include "heap.h"
#include <math.h>
#include <malloc.h>
#include <stdio.h>

static void allocator_bucket_push(buddy_bucket_t *bucket, buddy_pair_t pair) {
    if (bucket->size == bucket->capacity) {
        bucket->capacity *= 2;
        void *new_arr = realloc(bucket->arr, bucket->capacity * sizeof(buddy_pair_t));
        if (new_arr == NULL) {
            fprintf(stderr, "Error: Could not allocate memory\n");
            exit(1);
        }
        bucket->arr = new_arr;
    }

    bucket->arr[bucket->size++] = pair;
}

static buddy_pair_t allocator_bucket_remove_first(buddy_bucket_t bucket) {
    return bucket.arr[--bucket.size];
}

void DL_InitHeap() {
    heap = malloc(HEAP_SIZE * sizeof(int));
    allocator_buckets = malloc((HEAP_SIZE + 1) * sizeof(buddy_bucket_t));

    int x = ceil(log(HEAP_SIZE) / log(2));

    for (int i = 0; i <= x; i++) {
        allocator_buckets[i] = (buddy_bucket_t) {0, 1, malloc(1 * sizeof(buddy_pair_t))};
    }

    allocator_bucket_push(allocator_buckets, (buddy_pair_t) {0, HEAP_SIZE - 1});

    p_this = 0;
}

void DL_HeapFree() {
    for (int i = 0; i < sizeof(allocator_buckets); i++) {
        free(allocator_buckets[i].arr);
    }
}

void DL_SetpThis(int value) {
    p_this = value;
}

int DL_GetpThis() {
    return p_this;
}

void DL_HeapSetAbs(int index, int value) {
    heap[index] = value;
}

int DL_HeapGet(int index) {
    return heap[index];
}

void DL_HeapSetRel(int offset, int value) {
    heap[p_this + offset] = value;
}

int DL_HeapGetRel(int offset) {
    return heap[p_this + offset];
}

int DL_HeapAlloc(int size) {
    int x = ceil(log(size) / log(2));
    int i;
    buddy_pair_t temp;

    if (allocator_buckets[x].size != 0) {
        return allocator_bucket_remove_first(allocator_buckets[x]).lb;
    }

    for (i = x + 1; i < sizeof(allocator_buckets); i++) {
        if (allocator_buckets[i].size != 0)
            break;
    }
    if (i == sizeof(allocator_buckets)) {
        fprintf(stderr, "Error [OUT_OF_MEMORY]: Could not allocate memory\n");
        exit(1);
    }
    temp = allocator_bucket_remove_first(allocator_buckets[i]);
    i--;

    for (; i >= x; i--) {
        buddy_pair_t p1 = (buddy_pair_t) {temp.lb, temp.lb + (temp.ub - temp.lb) / 2};
        buddy_pair_t p2 = (buddy_pair_t) {temp.lb + (temp.ub - temp.lb + 1) / 2, temp.ub};

        allocator_bucket_push(&allocator_buckets[i], p1);
        allocator_bucket_push(&allocator_buckets[i], p2);

        temp = allocator_bucket_remove_first(allocator_buckets[i]);
    }

    return temp.lb;
}

int DL_HeapAllocAndPosition(int size) {
    int pos = DL_HeapAlloc(size);
    DL_SetpThis(pos);
    return pos;
}