package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCBinOp extends AbstractBytecode {
    public final String ops;
    public boolean flagged = false;

    public BCBinOp(String ops, int offset) {
        super(Type.BinOp, offset);
        this.ops = ops;
    }

    @Override
    public String toString() {
        return STR."BCBinOp(ops=\{ops}; offset=\{offset});";
    }

}
