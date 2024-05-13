package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCReturn extends AbstractBytecode {

    public BCReturn(int offset) {
        super(Type.Return, offset);
    }

    @Override
    public String toString() {
        return STR."BCReturn(offset=\{offset});";
    }

}
