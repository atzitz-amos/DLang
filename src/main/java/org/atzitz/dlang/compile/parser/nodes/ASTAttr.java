package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;

public class ASTAttr extends ASTNode {
    public final ASTNode cls;
    public final ASTIdentifier attr;

    public ASTAttr(ASTNode cls, ASTIdentifier attr, Location loc) {
        super(Type.Attr, loc);
        this.cls = cls;
        this.attr = attr;
    }

    public ASTAttr(ASTIdentifier cls, ASTIdentifier attr) {
        super(Type.Attr, Location.of(cls.loc, attr.loc));
        this.cls = cls;
        this.attr = attr;
    }

    @Override
    public String toString() {
        return STR."\{cls}.\{attr}";
    }
}
