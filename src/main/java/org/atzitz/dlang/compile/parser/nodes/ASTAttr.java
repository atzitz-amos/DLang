package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

public class ASTAttr extends ASTNode {
    public final ASTAttr parent;
    public final ASTIdentifier attr;

    public final ASTAttr base;
    public ASTAttr child = null;

    public ASTAttr(ASTAttr parent, ASTIdentifier attr, Location loc) {
        super(Type.Attr, loc);
        this.parent = parent;
        this.attr = attr;

        if (this.parent == null) {
            this.base = this;
        } else {
            this.base = (parent).base;
        }

        if (this.parent != null) {
            this.parent.child = this;
        }

    }

    public static ASTAttr asRoot(ASTIdentifier id) {
        return new ASTAttr(null, id, id.loc);
    }

    @Override
    public String toString() {
        return STR."\{parent}.\{attr}";
    }
}
