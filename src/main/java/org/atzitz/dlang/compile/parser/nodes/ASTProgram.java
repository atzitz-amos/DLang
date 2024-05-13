package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class ASTProgram extends ASTNode {

    private final Collection<ASTNode> body;

    public ASTProgram(Collection<ASTNode> body, Location loc) {
        super(Type.Program, loc);

        this.body = body;
    }

    @Override
    public String toString() {
        return STR."[\{loc}] ASTProgram {\n\{body.stream()
                .map(nd -> STR."\t\{nd.toString().replaceAll("\n", "\n\t")};")
                .collect(Collectors.joining("\n"))}\n}";
    }
}
