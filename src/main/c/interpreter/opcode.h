#ifndef DLANG_OPCODE_H
#define DLANG_OPCODE_H

#define IADD_OP "iadd"
#define ISUB_OP "isub"
#define IMUL_OP "imul"
#define IDIV_OP "idiv"
#define IMOD_OP "imod"

#define INEG_OP "ineg"
#define BNEG_OP "bneg"

#define IGT_OP "igt"
#define ILT_OP "ilt"
#define IGE_OP "ige"
#define ILE_OP "ile"
#define INE_OP "ine"
#define IEQ_OP "ieq"

#define IINC_OP "iinc"
#define IDEC_OP "idec"

#define LLD_OP "lld"
#define PLD_OP "pld"
#define VLD_OP "vld"
#define RLD_OP "rld"
#define GLD_OP "gld"

#define STORL_OP "storl"
#define STORG_OP "storg"
#define STORV_OP "storv"
#define STORR_OP "storr"

#define ICLD_OP "icld"
#define BCLD_OP "bcld"
#define RCLD_OP "rcld"

#define INVK_OP "invk"
#define INVKREL_OP "invkrel"
#define INVKCLS_OP "invkcls"

#define JMP_OP "jmp"
#define JMPF_OP "jmpf"
#define JMPT_OP "jmpt"

#define FALLOC_OP "falloc"
#define FINIT_OP "finit"

#define RET_OP "ret"
#define RETTHS_OP "retths"

#define OPCODE_IADD 0
#define OPCODE_ISUB 1
#define OPCODE_IMUL 2
#define OPCODE_IDIV 3
#define OPCODE_IMOD 4
#define OPCODE_INEG 5
#define OPCODE_BNEG 6
#define OPCODE_IGT 7
#define OPCODE_ILT 8
#define OPCODE_IGE 9
#define OPCODE_ILE 10
#define OPCODE_INE 11
#define OPCODE_IEQ 12
#define OPCODE_IINC 13
#define OPCODE_IDEC 14
#define OPCODE_LLD 15
#define OPCODE_PLD 16
#define OPCODE_VLD 17
#define OPCODE_RLD 18
#define OPCODE_GLD 19
#define OPCODE_STORL 20
#define OPCODE_STORG 21
#define OPCODE_STORV 22
#define OPCODE_STORR 23
#define OPCODE_ICLD 24
#define OPCODE_BCLD 25
#define OPCODE_RCLD 26
#define OPCODE_INVK 27
#define OPCODE_INVKREL 28
#define OPCODE_INVKCLS 29
#define OPCODE_JMP 30
#define OPCODE_JMPF 31
#define OPCODE_JMPT 32
#define OPCODE_FALLOC 33
#define OPCODE_FINIT 34
#define OPCODE_RET 35
#define OPCODE_RETTHS 36

typedef struct opcode {
    int offset;
    int arg0;
    int arg1;
    int arg2;
    int arg3;

    short opcode;
} opcode_t;

opcode_t opcode_from_string(int offset, char *instruction, int arg0, int arg1, int arg2, int arg3);

void op_iadd(opcode_t op);

void op_isub(opcode_t op);

void op_imul(opcode_t op);

void op_idiv(opcode_t op);

void op_imod(opcode_t op);

void op_ineg(opcode_t op);

void op_bneg(opcode_t op);

void op_igt(opcode_t op);

void op_ilt(opcode_t op);

void op_ige(opcode_t op);

void op_ile(opcode_t op);

void op_ine(opcode_t op);

void op_ieq(opcode_t op);

void op_iinc(opcode_t op);

void op_idec(opcode_t op);

void op_lld(opcode_t op);

void op_pld(opcode_t op);

void op_vld(opcode_t op);

void op_rld(opcode_t op);

void op_gld(opcode_t op);

void op_storl(opcode_t op);

void op_storg(opcode_t op);

void op_storv(opcode_t op);

void op_storr(opcode_t op);

void op_icld(opcode_t op);

void op_bcld(opcode_t op);

void op_rcld(opcode_t op);

void op_invk(opcode_t op);

void op_invkrel(opcode_t op);

void op_invkcls(opcode_t op);

void op_jmp(opcode_t op);

void op_jmpf(opcode_t op);

void op_jmpt(opcode_t op);

void op_falloc(opcode_t op);

void op_finit(opcode_t op);

void op_ret(opcode_t op);

void op_retths(opcode_t op);


#endif
