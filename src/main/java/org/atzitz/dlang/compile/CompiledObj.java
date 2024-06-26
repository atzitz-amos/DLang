package org.atzitz.dlang.compile;

import lombok.Getter;
import org.atzitz.dlang.compile.bytecode.ByteCode;
import org.atzitz.dlang.compile.bytecode.bytecodes.AbstractBytecode;
import org.atzitz.dlang.compile.parser.ScopeVisitor;

public class CompiledObj {

    private final AbstractBytecode[] array;

    private final @Getter ScopeVisitor scopes;

    private final @Getter int globalsC;


    public CompiledObj(ByteCode bytecode) {
        this.array = bytecode.getBytecodes().toArray(AbstractBytecode[]::new);
        this.scopes = bytecode.getScope();

        this.globalsC = bytecode.getScope().getRoot().getLocalObjects().size();
    }

    public AbstractBytecode at(int offset) {
        return array[offset];
    }

    public int size() {
        return array.length;
    }

}
