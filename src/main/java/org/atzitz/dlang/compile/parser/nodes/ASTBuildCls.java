package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

import java.util.List;

public class ASTBuildCls extends ASTNode {
    public final ASTIdentifier cls;
    public final List<ASTNode> params;

    public ASTBuildCls(ASTIdentifier cls, List<ASTNode> params, Location location) {
        super(Type.BuildCls, location);
        this.cls = cls;
        this.params = params;
    }

    @Override
    public String toString() {
        return STR."ASTBuildCls {\{cls}(\{params})}";
    }
}
