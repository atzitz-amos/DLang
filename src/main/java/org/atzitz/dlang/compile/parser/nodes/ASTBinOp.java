package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.Objects;

@Getter
public class ASTBinOp extends ASTNode {
    protected final ASTNode left;
    protected final String op;
    protected final ASTNode right;

    public ASTBinOp(ASTNode left, String op, ASTNode right, Location loc) {
        super(Type.BinOp, loc);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public ASTBinOp(ASTNode left, String op, ASTNode right, Type type, Location loc) {
        super(type, loc);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {
        return Objects.equals(op, "=") ? STR."[\{loc}] \{left} \{op} \{right}" : STR."\{left} \{op} \{right}";
    }
}
