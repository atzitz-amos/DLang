package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCLoadConst extends AbstractBytecode {
    public final int name;
    public boolean flagged = false;

    public BCLoadConst(int name, int offset) {
        super(Type.LoadConst, offset);
        this.name = name;
    }

    @Override
    public String toString() {
        return STR."BCLoadConst(name=\{name}; offset=\{offset});";
    }
}
