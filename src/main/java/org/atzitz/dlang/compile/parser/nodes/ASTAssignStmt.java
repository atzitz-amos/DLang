package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

public class ASTAssignStmt extends ASTBinOp {
    public ASTAssignStmt(ASTNode left, String op, ASTNode right, Location loc) {
        super(left, op, right, Type.AssignStmt, loc);
    }

    protected ASTAssignStmt(ASTNode left, String op, ASTNode right, Type type, Location loc) {
        super(left, op, right, type, loc);
    }
}
