package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCLoadParam extends AbstractBytecode {
    public final int id;
    public boolean flagged = false;

    public BCLoadParam(int id, int offset) {
        super(Type.LoadParam, offset);
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCLoadParam(id=\{id}; offset=\{offset});";
    }
}
