package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCLoadDynamic extends AbstractBytecode {
    public final int id;
    public boolean flagged = false;

    public BCLoadDynamic(int id, int offset) {
        super(Type.LoadDynamic, offset);
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCLoadDynamic(id=\{id}; offset=\{offset});";
    }
}
