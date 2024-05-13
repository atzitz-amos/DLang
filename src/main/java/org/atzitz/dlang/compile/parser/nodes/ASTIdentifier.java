package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

@Getter
public class ASTIdentifier extends ASTNode {
    public final String name;

    public ASTIdentifier(String name, Location loc) {
        super(Type.Identifier, loc);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
