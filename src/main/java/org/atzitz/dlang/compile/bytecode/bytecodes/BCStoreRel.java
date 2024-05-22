package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCStoreRel extends AbstractBytecode {
    public final int id;
    public final int obj;

    public BCStoreRel(int obj, int id, int offset) {
        super(Type.StoreRel, offset);
        this.obj = obj;
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."BCStoreRel(obj=\{obj}; id=\{id}; offset=\{offset});";
    }
}
