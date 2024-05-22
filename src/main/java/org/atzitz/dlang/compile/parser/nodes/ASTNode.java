package org.atzitz.dlang.compile.parser.nodes;

import org.atzitz.dlang.compile.Location;
import org.jetbrains.annotations.NotNull;

public abstract class ASTNode {

    public final @NotNull Type type;
    public final Location loc;

    protected ASTNode(@NotNull Type type, Location loc) {
        this.type = type;
        this.loc = loc;
    }

    public enum Type {
        Program,

        ExprStmt, DeclareStmt, IfStmt, WhileStmt,

        Number, Identifier,

        BinOp, AssignStmt, ExchangeAssignStmt, ExchangeGroup, FuncDef, ClassDef, Parameter, Attr, AttrAssignStmt,
        FunctionCallStmt,
        ReturnStmt, BuildCls, Literal, UnOp, Array, Boolean, ArrayAccess, Comp
    }

}
