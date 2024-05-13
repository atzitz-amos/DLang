package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

public class BCCompare extends AbstractBytecode {
    public final String op;
    @Setter
    public boolean flagged;

    public BCCompare(String op, int offset) {
        super(Type.Compare, offset);
        this.op = op;
    }

    @Override
    public String toString() {
        return STR."BCCompare(op=\{op}; flagged=\{flagged}; offset=\{offset});";
    }

}
