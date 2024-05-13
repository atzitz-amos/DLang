package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCJump extends AbstractBytecode {
    public final int jump;

    public BCJump(int jump, int offset) {
        super(Type.Jump, offset);
        this.jump = jump;
    }

    @Override
    public String toString() {
        return STR."BCJump(to=\{jump}; offset=\{offset});";
    }

}
