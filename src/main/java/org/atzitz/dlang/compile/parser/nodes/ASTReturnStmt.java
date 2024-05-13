package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;
import org.atzitz.dlang.compile.parser.nodes.ASTNode;

public class ASTReturnStmt extends ASTNode {
    public final ASTNode expr;

    public ASTReturnStmt(ASTNode expr, Location loc) {
        super(Type.ReturnStmt, loc);
        this.expr = expr;
    }

    @Override
    public String toString() {
        return STR."ASTReturnStmt{\{expr}}";
    }
}
