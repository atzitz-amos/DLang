package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCInitFunc extends AbstractBytecode {
    public final int localc;

    public BCInitFunc(int localc, int offset) {
        super(AbstractBytecode.Type.FuncInit, offset);
        this.localc = localc;
    }

    @Override
    public String toString() {
        return STR."BCInitFunc(localc=\{localc});";
    }
}
