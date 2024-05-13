package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCBinOp extends AbstractBytecode {
    public final String op;
    public boolean flagged = false;

    public BCBinOp(String op, int offset) {
        super(Type.BinOp, offset);
        this.op = op;
    }

    @Override
    public String toString() {
        return STR."BCBinOp(op=\{op}; offset=\{offset});";
    }

}
