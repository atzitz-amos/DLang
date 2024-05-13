package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCStoreGlobal extends AbstractBytecode {
    public final int id;

    public BCStoreGlobal(int id, int offset) {
        super(Type.StoreGlobal, offset);
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCStoreGlobal(id=\{id}; offset=\{offset});";
    }
}
