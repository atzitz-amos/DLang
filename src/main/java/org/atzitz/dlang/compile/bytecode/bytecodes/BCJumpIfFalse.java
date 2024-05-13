package org.atzitz.dlang.compile.bytecode.bytecodes;

public class BCJumpIfFalse extends AbstractBytecode {
    public final int jump;

    public BCJumpIfFalse(int jump, int offset) {
        super(Type.JumpIfFalse, offset);
        this.jump = jump;
    }

    @Override
    public String toString() {
        return STR."BCJumpIfFalse(to=\{jump}; offset=\{offset});";
    }

}
