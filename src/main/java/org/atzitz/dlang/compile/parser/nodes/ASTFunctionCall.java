package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

import java.util.List;

public class ASTFunctionCall extends ASTNode {
    public final ASTNode id;
    public final List<ASTNode> params;

    public ASTFunctionCall(ASTNode id, List<ASTNode> params, Location location) {
        super(Type.FunctionCallStmt, location);
        this.id = id;
        this.params = params;
    }

    @Override
    public String toString() {
        return STR."\{id}(\{params})";
    }
}
