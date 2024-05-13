package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class ASTIfStmt extends ASTNode {
    private final ASTExprStmt expr;
    private final Collection<ASTNode> body;
    private ASTIfStmt alternative;

    public ASTIfStmt(ASTExprStmt expr, Collection<ASTNode> body, ASTIfStmt alternative, Location loc) {
        super(Type.IfStmt, loc);

        this.expr = expr;
        this.body = body;
        this.alternative = alternative;
    }

    public ASTIfStmt(ASTExprStmt expr, Collection<ASTNode> body, Location loc) {
        this(expr, body, null, loc);
    }

    @Override
    public String toString() {
        return STR."[\{loc}] ASTIfStmt(\{expr}) {\n\{body.stream()
                .map(nd -> STR."\t\{nd.toString().replaceAll("\n", "\n\t")};")
                .collect(Collectors.joining("\n"))}\n}\{alternative != null ? STR." \{alternative}" : ""}";
    }

    public ASTIfStmt alternative(ASTIfStmt stmt) {
        alternative = stmt;
        return stmt;
    }
}
