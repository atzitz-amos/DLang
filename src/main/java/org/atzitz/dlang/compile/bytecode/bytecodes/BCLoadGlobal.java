package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCLoadGlobal extends AbstractBytecode {
    public final int id;
    public boolean flagged = false;

    public BCLoadGlobal(int id, int offset) {
        super(Type.LoadGlobal, offset);
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCLoadGlobal(id=\{id}; offset=\{offset});";
    }
}
