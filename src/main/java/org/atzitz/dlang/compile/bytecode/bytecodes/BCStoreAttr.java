package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCStoreAttr extends AbstractBytecode {
    public final int id;
    public final int obj;

    public BCStoreAttr(int obj, int id, int offset) {
        super(Type.StoreAttr, offset);
        this.obj = obj;
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCStoreAttr(obj=\{obj}; id=\{id}; offset=\{offset});";
    }
}
