cmake_minimum_required(VERSION 3.28)
project(DLang C)

set(CMAKE_C_STANDARD 11)

include_directories(src/main/c/interpreter)

add_executable(DLang
        src/main/c/interpreter/opcode.c
        src/main/c/interpreter/opcode.h
        src/main/c/interpreter/memory.h
        src/main/c/interpreter/memory.c
        src/main/c/interpreter/main.c
        src/main/c/interpreter/heap.h
        src/main/c/interpreter/heap.c
        src/main/c/interpreter/instructions.h
        src/main/c/interpreter/instructions.c
)