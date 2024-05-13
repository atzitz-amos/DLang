package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class ASTWhileStmt extends ASTNode {
    private final ASTExprStmt expr;
    private final Collection<ASTNode> body;

    public ASTWhileStmt(ASTExprStmt expr, Collection<ASTNode> body, Location loc) {
        super(Type.WhileStmt, loc);

        this.expr = expr;
        this.body = body;
    }

    @Override
    public String toString() {
        return STR."[\{loc}] ASTWhileStmt(\{expr}) {\n\{body.stream()
                .map(nd -> STR."\t\{nd.toString().replaceAll("\n", "\n\t")};")
                .collect(Collectors.joining("\n"))}\n}";
    }
}
