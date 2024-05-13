package org.atzitz.dlang.compile.bytecode.bytecodes;

import java.util.Arrays;

public class BCExchangeAssign extends AbstractBytecode {

    public final int[] ids;
    public final int size;

    public BCExchangeAssign(int[] ids, int size, int offset) {
        super(Type.ExchangeAssign, offset);
        this.ids = ids;
        this.size = size;
    }

    @Override
    public String toString() {
        return STR."BCExchange(names=[\{Arrays.toString(ids)}]; num=\{size}; offset=\{offset});";
    }
}
