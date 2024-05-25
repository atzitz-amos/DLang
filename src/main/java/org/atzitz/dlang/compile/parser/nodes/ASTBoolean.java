package org.atzitz.dlang.compile.parser.nodes;


import org.atzitz.dlang.compile.Location;

public class ASTBoolean extends ASTNode {
    public final boolean value;

    public ASTBoolean(boolean value, Location loc) {
        super(Type.Boolean, loc);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
