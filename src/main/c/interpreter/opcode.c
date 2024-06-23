#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "opcode.h"

#include "memory.h"
#include "heap.h"
#include "instructions.h"


static void save_current_state(int argc) {
    DL_StackPush(DL_GetpLocal());
    DL_StackPush(DL_GetpParam());
    DL_StackPush(DL_GetpThis());
    DL_StackPush(instruct_pc_get());

    DL_SetpParam(DL_GetpSP() - 4 - argc);
    DL_SetpLocal(DL_GetpSP());
}

void op_iadd(opcode_t op) {
    DL_StackPush(DL_StackPop() + DL_StackPop());
}

void op_isub(opcode_t op) {
    int a = -DL_StackPop();
    int b = DL_StackPop();
    DL_StackPush(a + b);
}

void op_imul(opcode_t op) {
    DL_StackPush(DL_StackPop() * DL_StackPop());
}

void op_idiv(opcode_t op) {
    int divisor = DL_StackPop();
    int dividend = DL_StackPop();
    DL_StackPush(dividend / divisor);
}

void op_imod(opcode_t op) {
    int divisor = DL_StackPop();
    int dividend = DL_StackPop();
    DL_StackPush(dividend % divisor);
}

void op_ineg(opcode_t op) {
    DL_StackPush(-DL_StackPop());
}

void op_bneg(opcode_t op) {
    DL_StackPush(!DL_StackPop());
}

void op_igt(opcode_t op) {
    DL_StackPush(DL_StackPop() > DL_StackPop());
}

void op_ilt(opcode_t op) {
    DL_StackPush(DL_StackPop() < DL_StackPop());
}

void op_ige(opcode_t op) {
    DL_StackPush(DL_StackPop() >= DL_StackPop());
}

void op_ile(opcode_t op) {
    DL_StackPush(DL_StackPop() <= DL_StackPop());
}

void op_ine(opcode_t op) {
    DL_StackPush(DL_StackPop() != DL_StackPop());
}

void op_ieq(opcode_t op) {
    DL_StackPush(DL_StackPop() == DL_StackPop());
}

void op_iinc(opcode_t op) {
    // TODO
}

void op_idec(opcode_t op) {
    // TODO
}

void op_lld(opcode_t op) {
    DL_StackPush(DL_LocalGet(op.arg0));
}

void op_pld(opcode_t op) {
    DL_StackPush(DL_ParamGet(op.arg0));
}

void op_vld(opcode_t op) {
    DL_StackPush(DL_HeapGetRel(op.arg0));
}

void op_rld(opcode_t op) {
    DL_StackPush(DL_HeapGet(op.arg0 + DL_StackPop()));
}

void op_gld(opcode_t op) {
    DL_StackPush(DL_MemorySeek(op.arg0));
}

void op_storl(opcode_t op) {
    DL_LocalSet(op.arg0, DL_StackPop());
}

void op_storg(opcode_t op) {
    DL_MemorySet(op.arg0, DL_StackPop());
}

void op_storv(opcode_t op) {
    DL_HeapSetRel(op.arg0, DL_StackPop());
}

void op_storr(opcode_t op) {
    DL_HeapSetAbs(op.arg0 + DL_StackPop(), DL_StackPop());
}

void op_icld(opcode_t op) {
    DL_StackPush(op.arg0);
}

void op_bcld(opcode_t op) {
    DL_StackPush(op.arg0);
}

void op_rcld(opcode_t op) {
    DL_StackPush(op.arg0);
}

void op_invk(opcode_t op) {
    save_current_state(op.arg1);  // Save current state

    instruct_pc_jmp(DL_HeapGetRel(op.arg0));  // Jump
}

void op_invkrel(opcode_t op) {
    int tos = DL_StackPop();
    save_current_state(op.arg1);  // Save current state

    instruct_pc_jmp(DL_HeapGet(tos + op.arg0));  // Jump
}

void op_invkcls(opcode_t op) {
    int pos = DL_HeapGetRel(op.arg0);
    save_current_state(op.arg1);  // Save current state

    DL_HeapAllocAndPosition(op.arg2); // Prepare HEAP
    instruct_pc_jmp(pos);  // Jump
}

void op_jmp(opcode_t op) {
    instruct_pc_jmp(op.arg0 - 1);
}

void op_jmpf(opcode_t op) {
    if (!DL_StackPop()) {
        instruct_pc_jmp(op.arg0 - 1);
    }
}

void op_jmpt(opcode_t op) {
    if (DL_StackPop()) {
        instruct_pc_jmp(op.arg0 - 1);
    }
}

void op_falloc(opcode_t op) {
    DL_HeapSetRel(op.arg0, op.offset);
    instruct_pc_jmp(op.arg1 - 1);
}

void op_finit(opcode_t op) {
    DL_SetpSP(DL_GetpSP() + op.arg0);
}

void op_ret(opcode_t op) {
    int retValue = DL_StackPop();
    int param = DL_GetpParam();
    DL_SetpSP(DL_GetpLocal());

    instruct_pc_jmp(DL_StackPop());
    DL_SetpThis(DL_StackPop());
    DL_SetpParam(DL_StackPop());
    DL_SetpLocal(DL_StackPop());

    DL_SetpSP(param);

    DL_StackPush(retValue);
}

void op_retths(opcode_t op) {
    int retValue = DL_GetpThis();
    int param = DL_GetpParam();
    DL_SetpSP(DL_GetpLocal());

    instruct_pc_jmp(DL_StackPop());
    DL_SetpThis(DL_StackPop());
    DL_SetpParam(DL_StackPop());
    DL_SetpLocal(DL_StackPop());

    DL_SetpSP(param);

    DL_StackPush(retValue);
}

opcode_t opcode_from_string(int offset, char *instruction, int arg0, int arg1, int arg2, int arg3) {
    if (strcmp(instruction, IADD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IADD};
    } else if (strcmp(instruction, ISUB_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_ISUB};
    } else if (strcmp(instruction, IMUL_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IMUL};
    } else if (strcmp(instruction, IDIV_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IDIV};
    } else if (strcmp(instruction, IMOD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IMOD};
    } else if (strcmp(instruction, INEG_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_INEG};
    } else if (strcmp(instruction, BNEG_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_BNEG};
    } else if (strcmp(instruction, IGT_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IGT};
    } else if (strcmp(instruction, ILT_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_ILT};
    } else if (strcmp(instruction, IGE_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IGE};
    } else if (strcmp(instruction, ILE_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_ILE};
    } else if (strcmp(instruction, INE_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_INE};
    } else if (strcmp(instruction, IEQ_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IEQ};
    } else if (strcmp(instruction, IINC_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IINC};
    } else if (strcmp(instruction, IDEC_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_IDEC};
    } else if (strcmp(instruction, LLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_LLD};
    } else if (strcmp(instruction, PLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_PLD};
    } else if (strcmp(instruction, VLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_VLD};
    } else if (strcmp(instruction, RLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_RLD};
    } else if (strcmp(instruction, GLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_GLD};
    } else if (strcmp(instruction, STORL_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_STORL};
    } else if (strcmp(instruction, STORG_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_STORG};
    } else if (strcmp(instruction, STORV_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_STORV};
    } else if (strcmp(instruction, STORR_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_STORR};
    } else if (strcmp(instruction, ICLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_ICLD};
    } else if (strcmp(instruction, BCLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_BCLD};
    } else if (strcmp(instruction, RCLD_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_RCLD};
    } else if (strcmp(instruction, INVK_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_INVK};
    } else if (strcmp(instruction, INVKREL_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_INVKREL};
    } else if (strcmp(instruction, INVKCLS_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_INVKCLS};
    } else if (strcmp(instruction, JMP_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_JMP};
    } else if (strcmp(instruction, JMPF_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_JMPF};
    } else if (strcmp(instruction, JMPT_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_JMPT};
    } else if (strcmp(instruction, FALLOC_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_FALLOC};
    } else if (strcmp(instruction, FINIT_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_FINIT};
    } else if (strcmp(instruction, RET_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_RET};
    } else if (strcmp(instruction, RETTHS_OP) == 0) {
        return (opcode_t) {offset, arg0, arg1, arg2, arg3, OPCODE_RETTHS};
    }
    fprintf(stderr, "Unknown instruction: %s\n", instruction);
    exit(1);
}
