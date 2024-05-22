package org.atzitz.dlang.compile.bytecode.bytecodes;

import lombok.Setter;

@Setter
public class BCBuildCls extends AbstractBytecode {
    public final int id;
    public final int argc;
    public final int localc;
    public boolean flagged = false;

    public BCBuildCls(int id, int argc, int localc, int offset) {
        super(Type.BuildCls, offset);
        this.id = id;
        this.argc = argc;
        this.localc = localc;
    }

    @Override
    public String toString() {
        return STR."BCBuildCls(id=\{id}; nparam=\{argc}; localc=\{localc}; offset=\{offset});";
    }
}
