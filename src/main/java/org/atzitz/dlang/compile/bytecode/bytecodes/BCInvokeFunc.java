package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCInvokeFunc extends AbstractBytecode {
    public final int obj;
    public final int id;
    private final int nparam;
    public boolean flagged = false;

    public BCInvokeFunc(int obj, int id, int nparam, int offset) {
        super(Type.InvokeFunc, offset);
        this.obj = obj;
        this.id = id;
        this.nparam = nparam;
    }

    @Override
    public String toString() {
        return STR."BCInvokeFunc(obj=\{obj}; id=\{id}; nparam=\{nparam}; offset=\{offset});";
    }
}
