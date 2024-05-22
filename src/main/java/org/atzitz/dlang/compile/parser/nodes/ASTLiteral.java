package org.atzitz.dlang.compile.parser.nodes;


import lombok.Getter;
import org.atzitz.dlang.compile.Location;

@Getter
public class ASTLiteral extends ASTNode {
    private final String value;

    public ASTLiteral(String value, Location loc) {
        super(Type.Literal, loc);
        this.value = value;
    }

    @Override
    public String toString() {
        return STR."\"\{value}\"";
    }
}
