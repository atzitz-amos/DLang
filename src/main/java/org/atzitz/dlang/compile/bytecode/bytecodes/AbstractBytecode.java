package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Data;

@Data
public abstract class AbstractBytecode {
    public final int type;
    public final int offset;

    protected AbstractBytecode(int type, int offset) {
        this.type = type;
        this.offset = offset;
    }

    public static class Type {

        public static final int LoadGlobal = 0;
        public static final int LoadDynamic = 1;
        public static final int LoadConst = 2;
        public static final int LoadRel = 3;
        public static final int LoadThis = 4;
        public static final int LoadParam = 5;

        public static final int StoreGlobal = 6;
        public static final int StoreDynamic = 7;
        public static final int StoreRel = 8;
        public static final int ExchangeAssign = 9;

        public static final int JumpIfFalse = 10;
        public static final int Jump = 11;

        public static final int BinOp = 12;
        public static final int Compare = 13;

        public static final int InvokeFunc = 14;
        public static final int BuildCls = 15;

        public static final int FuncAlloc = 16;

        public static final int FuncInit = 17;

        public static final int Return = 18;

    }

}
