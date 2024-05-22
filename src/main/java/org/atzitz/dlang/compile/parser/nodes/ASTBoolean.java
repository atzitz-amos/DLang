package org.atzitz.dlang.compile.parser.nodes;


import lombok.Getter;
import org.atzitz.dlang.compile.Location;

@Getter
public class ASTBoolean extends ASTNode {
    private final boolean value;

    public ASTBoolean(boolean value, Location loc) {
        super(Type.Boolean, loc);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
