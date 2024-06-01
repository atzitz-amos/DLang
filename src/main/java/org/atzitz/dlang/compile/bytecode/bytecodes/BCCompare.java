package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

public class BCCompare extends AbstractBytecode {
    public final String ops;
    @Setter
    public boolean flagged;

    public BCCompare(String ops, int offset) {
        super(Type.Compare, offset);
        this.ops = ops;
    }

    @Override
    public String toString() {
        return STR."BCCompare(ops=\{ops}; flagged=\{flagged}; offset=\{offset});";
    }

}
