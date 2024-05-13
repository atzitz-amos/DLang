package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCLoadAttr extends AbstractBytecode {
    private final int obj;
    public final int id;
    public boolean flagged = false;

    public BCLoadAttr(int obj, int id, int offset) {
        super(Type.LoadAttr, offset);
        this.obj = obj;
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCLoadAttr(obj=\{obj}; id=\{id}; offset=\{offset});";
    }
}
