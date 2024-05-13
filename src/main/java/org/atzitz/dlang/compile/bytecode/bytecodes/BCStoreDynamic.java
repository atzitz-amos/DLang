package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCStoreDynamic extends AbstractBytecode {
    public final int id;

    public BCStoreDynamic(int id, int offset) {
        super(Type.StoreDynamic, offset);
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCStoreDynamic(id=\{id}; offset=\{offset});";
    }
}
