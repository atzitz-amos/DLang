package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

import java.util.List;

public class ASTAttrFunctionCall extends ASTNode {
    public final ASTAttr attr;
    public final List<ASTNode> params;

    public ASTAttrFunctionCall(ASTAttr attr, List<ASTNode> params, Location location) {
        super(Type.AttrFunctionCallStmt, location);
        this.attr = attr;
        this.params = params;
    }

    @Override
    public String toString() {
        return STR."\{attr}(\{params})";
    }
}
