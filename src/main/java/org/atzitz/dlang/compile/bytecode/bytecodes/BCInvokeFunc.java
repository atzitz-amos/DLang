package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCInvokeFunc extends AbstractBytecode {
    public final int obj;
    public final int id;
    public final int argc;
    public boolean flagged = false;

    public BCInvokeFunc(int obj, int id, int argc, int offset) {
        super(Type.InvokeFunc, offset);
        this.obj = obj;
        this.id = id;
        this.argc = argc;
    }

    @Override
    public String toString() {
        return STR."BCInvokeFunc(obj=\{obj}; id=\{id}; nparam=\{argc}; offset=\{offset});";
    }
}
