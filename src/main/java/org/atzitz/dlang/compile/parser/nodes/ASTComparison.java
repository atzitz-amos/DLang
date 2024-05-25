package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

@Getter
public class ASTComparison extends ASTBinaryOp {
    public ASTComparison(ASTNode left, String op, ASTNode right, Location loc) {
        super(left, op, right, Type.Comparison, loc);
    }

}
