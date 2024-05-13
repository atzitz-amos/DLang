package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCBuildCls extends AbstractBytecode {
    public final int id;
    private final int nparam;
    public boolean flagged = false;

    public BCBuildCls(int id, int nparam, int offset) {
        super(Type.BuildCls, offset);
        this.id = id;
        this.nparam = nparam;
    }

    @Override
    public String toString() {
        return STR."BCBuildCls(id=\{id}; nparam=\{nparam}; offset=\{offset});";
    }
}
