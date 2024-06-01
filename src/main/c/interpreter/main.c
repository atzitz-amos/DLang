
#include "instructions.h"
#include "memory.h"
#include "heap.h"

#include "opcode.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

char *normalize_arg(char *str) {
    char *end;
    /* skip leading whitespace */
    while (*str == ' ') {
        str = str + 1;
    }
    /* remove trailing whitespace */
    end = str + strlen(str) - 1;
    while (end > str && *end == ' ' || *end == '\n') {
        end = end - 1;
    }
    /* write null character */
    *(end + 1) = '\0';
    return str;
}

void collect_args(char *token, char *result) {
    if (token == NULL) {
        strcpy(result, "0");
        return;
    }

    token = normalize_arg(token);

    if (strcmp(token, "//") == 0) {
        while (token != NULL) {
            token = strtok(NULL, " ");
        }
        strcpy(result, "0");
    } else {
        if (token[0] == '#')
            strcpy(result, token + 1);
        else if (strlen(token) == 0)
            strcpy(result, "0");
        else
            strcpy(result, token);
    }
}

opcode_t build_instr(char buffer[]) {
    char offset[10] = "";

    char instr[10] = "";

    char arg0[32] = "";
    char arg1[32] = "";
    char arg2[32] = "";
    char arg3[32] = "";

    char *token = strtok(buffer, " ");
    strncpy(offset, token, strlen(token) - 1);

    token = strtok(NULL, " ");
    if (token[strlen(token) - 1] == '\n') {
        strncpy(instr, token, strlen(token) - 1);
        strcpy(arg0, "0");
        strcpy(arg1, "0");
        strcpy(arg2, "0");
        strcpy(arg3, "0");
    } else {
        strncpy(instr, token, strlen(token));

        collect_args(strtok(NULL, " "), arg0);
        collect_args(strtok(NULL, " "), arg1);
        collect_args(strtok(NULL, " "), arg2);
        collect_args(strtok(NULL, " "), arg3);

    }
    printf("Offset: %s; instr: %s; arg0: %s; arg1: %s; arg2: %s; arg3: %s;\n", offset, instr, arg0, arg1, arg2,
           arg3);

    char *dummy;

    return opcode_from_string(strtol(offset, &dummy, 10), instr, strtol(arg0, &dummy, 10), strtol(arg1, &dummy, 10),
                              strtol(arg2, &dummy, 10), strtol(arg3, &dummy, 10));
}

struct instruction_memory load_bytecode_and_init_memory(char *filename) {
    FILE *fptr = fopen(filename, "r");
    if (fptr == NULL) {
        fprintf(stderr, "Error: Could not open file\n");
        exit(1);
    }

    char buffer[512];

    size_t size = 0;
    size_t capacity = 1;
    opcode_t *ops = malloc(capacity * sizeof(opcode_t));

    fgets(buffer, sizeof(buffer), fptr);
    char *globaln = malloc(10 * sizeof(char));
    strncpy(globaln, buffer + 9, strlen(buffer) - 9);
    fgets(buffer, sizeof(buffer), fptr);
    char *globalc = malloc(10 * sizeof(char));
    strncpy(globalc, buffer + 9, strlen(buffer) - 9);

    while (fgets(buffer, sizeof(buffer), fptr) != NULL) {
        if (++size > capacity) {
            capacity *= 2;
            opcode_t *temp = realloc(ops, capacity * sizeof(opcode_t));
            if (temp == NULL) {
                fprintf(stderr, "Error: Could not allocate memory\n");
                exit(1);
            }
            ops = temp;
        }
        ops[size - 1] = build_instr(buffer);
    }

    fclose(fptr);

    struct instruction_memory instr_memory = (struct instruction_memory) {size, ops};

    DL_InitMemory(strtol(globalc, NULL, 10));
    DL_InitHeap();
    DL_HeapAllocAndPosition(strtol(globaln, NULL, 10));

    return instr_memory;
}

double interpret(struct instruction_memory instr_memory) {
    clock_t start, end;

    start = clock();
    while (instruct_pc_get() < instr_memory.size) {
        opcode_t op = instr_memory.ops[instruct_pc_get()];
        (*op.handlr)(&op);

        instruct_pc_incr();
    }
    end = clock();
    return ((double) (end - start)) / CLOCKS_PER_SEC;
}

void free_and_quit(struct instruction_memory memory) {
    free(memory.ops);
    DL_HeapFree();
}

int main() {
    struct instruction_memory instr_memory = load_bytecode_and_init_memory(
            "C:\\Users\\amosa\\IdeaProjects\\DLang\\out.txt");

    double elapsed = interpret(instr_memory);

    printf("Result: %i\n", DL_MemorySeek(1));
    printf("Elapsed time: %f\n", elapsed);

    free_and_quit(instr_memory);
    printf("Program finished\n");
    return 0;
}