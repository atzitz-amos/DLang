package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;

@Getter
public class ASTExprStmt extends ASTNode {
    private final ASTNode content;

    public ASTExprStmt(ASTNode content) {
        super(Type.ExprStmt, content.loc);
        this.content = content;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
