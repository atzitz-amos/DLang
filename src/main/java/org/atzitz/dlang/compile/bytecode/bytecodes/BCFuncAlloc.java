package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCFuncAlloc extends AbstractBytecode {
    public final int until;
    public final int pointer;

    public BCFuncAlloc(int until, int pointer, int offset) {
        super(Type.FuncAlloc, offset);
        this.until = until;
        this.pointer = pointer;
    }

    @Override
    public String toString() {
        return STR."BCFuncAlloc(until=\{until}; pointer=\{pointer}; offset=\{offset});";
    }

}
