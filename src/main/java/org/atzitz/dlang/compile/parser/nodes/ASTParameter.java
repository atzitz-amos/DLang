package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

public class ASTParameter extends ASTNode {
    public final ASTIdentifier type;
    public final ASTIdentifier id;

    public ASTParameter(ASTIdentifier type, ASTIdentifier id) {
        super(Type.Parameter, Location.of(type.loc, id.loc));
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."\{type} \{id}";
    }
}
