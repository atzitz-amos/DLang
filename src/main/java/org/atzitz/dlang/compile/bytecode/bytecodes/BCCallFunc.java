package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCCallFunc extends AbstractBytecode {
    public final int id;
    public final int argc;
    public boolean flagged = false;

    public BCCallFunc(int id, int argc, int offset) {
        super(Type.CallFunc, offset);
        this.id = id;
        this.argc = argc;
    }

    @Override
    public String toString() {
        return STR."BCCallFunc(id=\{id}; nparam=\{argc}; offset=\{offset});";
    }
}
