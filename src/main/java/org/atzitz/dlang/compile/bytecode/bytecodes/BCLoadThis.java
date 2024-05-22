package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCLoadThis extends AbstractBytecode {

    public BCLoadThis(int offset) {
        super(Type.LoadThis, offset);
    }

    @Override
    public String toString() {
        return STR."BCLoadThis(offset=\{offset});";
    }

}
