package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

public class ASTAttrAssignStmt extends ASTAssignStmt {
    public ASTAttrAssignStmt(ASTAttr attr, String ops, ASTNode right) {
        super(attr, ops, right, Type.AttrAssignStmt, Location.of(attr.loc, right.loc));
    }

    public ASTAttr getAttr() {
        return (ASTAttr) left;
    }
}
