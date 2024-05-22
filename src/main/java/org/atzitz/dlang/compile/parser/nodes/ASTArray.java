package org.atzitz.dlang.compile.parser.nodes;


import org.atzitz.dlang.compile.Location;

import java.util.Collection;

public class ASTArray extends ASTNode {
    public final Collection<ASTNode> content;

    public ASTArray(Collection<ASTNode> content, Location loc) {
        super(Type.Array, loc);
        this.content = content;
    }

    @Override
    public String toString() {
        return STR."\{content}";
    }
}
