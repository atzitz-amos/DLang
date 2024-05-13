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

        public static final int LoadConst = 0;
        public static final int BinOp = 1;
        public static final int Compare = 2;
        public static final int ExchangeAssign = 3;
        public static final int JumpIfFalse = 4;
        public static final int Jump = 5;
        public static final int StoreDynamic = 6;
        public static final int LoadAttr = 7;
        public static final int InvokeFunc = 8;
        public static final int CallFunc = 9;
        public static final int Return = 10;
        public static final int BuildCls = 11;
        public static final int FuncAlloc = 12;
        public static final int StoreAttr = 13;
        public static final int LoadParam = 14;
        public static final int StoreGlobal = 15;
        public static final int LoadGlobal = 16;
        public static final int LoadDynamic = 17;
        public static final int FuncInit = 18;
    }

}
