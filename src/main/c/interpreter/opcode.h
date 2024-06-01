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

typedef struct opcode {
    int offset;
    int arg0;
    int arg1;
    int arg2;
    int arg3;

    void (*handlr)(struct opcode *);
} opcode_t;

opcode_t opcode_from_string(int offset, char *instruction, int arg0, int arg1, int arg2, int arg3);

void op_iadd(opcode_t *op);

void op_isub(opcode_t *op);

void op_imul(opcode_t *op);

void op_idiv(opcode_t *op);

void op_imod(opcode_t *op);

void op_ineg(opcode_t *op);

void op_bneg(opcode_t *op);

void op_igt(opcode_t *op);

void op_ilt(opcode_t *op);

void op_ige(opcode_t *op);

void op_ile(opcode_t *op);

void op_ine(opcode_t *op);

void op_ieq(opcode_t *op);

void op_iinc(opcode_t *op);

void op_idec(opcode_t *op);

void op_lld(opcode_t *op);

void op_pld(opcode_t *op);

void op_vld(opcode_t *op);

void op_rld(opcode_t *op);

void op_gld(opcode_t *op);

void op_storl(opcode_t *op);

void op_storg(opcode_t *op);

void op_storv(opcode_t *op);

void op_storr(opcode_t *op);

void op_icld(opcode_t *op);

void op_bcld(opcode_t *op);

void op_rcld(opcode_t *op);

void op_invk(opcode_t *op);

void op_invkrel(opcode_t *op);

void op_invkcls(opcode_t *op);

void op_jmp(opcode_t *op);

void op_jmpf(opcode_t *op);

void op_jmpt(opcode_t *op);

void op_falloc(opcode_t *op);

void op_finit(opcode_t *op);

void op_ret(opcode_t *op);

void op_retths(opcode_t *op);

#endif
