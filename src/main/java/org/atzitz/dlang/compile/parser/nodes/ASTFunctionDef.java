package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class ASTFunctionDef extends ASTNode {

    public final ASTIdentifier returnType;
    public final ASTIdentifier name;
    public final Collection<ASTParameter> params;
    public final Collection<ASTNode> body;

    public ASTFunctionDef(ASTIdentifier returnType, ASTIdentifier name, Collection<ASTParameter> params, Collection<ASTNode> body, Location loc) {
        super(Type.FuncDef, loc);
        this.returnType = returnType;
        this.name = name;
        this.params = params;

        this.body = body;
    }

    @Override
    public String toString() {
        return STR."[\{loc}] ASTFuncDef \{name}(\{params}) {\n\{body.stream()
                .map(nd -> STR."\t\{nd.toString().replaceAll("\n", "\n\t")};")
                .collect(Collectors.joining("\n"))}\n}";
    }
}
