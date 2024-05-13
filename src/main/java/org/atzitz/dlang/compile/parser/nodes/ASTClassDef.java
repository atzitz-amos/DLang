package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class ASTClassDef extends ASTNode {

    private final Collection<ASTNode> body;
    private final ASTIdentifier id;

    public ASTClassDef(ASTIdentifier id, Collection<ASTNode> body, Location loc) {
        super(Type.ClassDef, loc);

        this.body = body;
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."[\{loc}] ASTClass('\{id}') {\n\{body.stream()
                .map(nd -> STR."\t\{nd.toString().replaceAll("\n", "\n\t")};")
                .collect(Collectors.joining("\n"))}\n}";
    }
}
