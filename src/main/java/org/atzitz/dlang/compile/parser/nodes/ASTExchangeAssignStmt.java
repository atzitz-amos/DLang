package org.atzitz.dlang.compile.parser.nodes;

import lombok.Getter;
import org.atzitz.dlang.compile.Location;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ASTExchangeAssignStmt extends ASTAssignStmt {

    public ASTExchangeAssignStmt(List<ASTNode> selectors, List<ASTNode> values, Location loc) {
        super(new ExchangeGroup(selectors), "=", new ExchangeGroup(values), loc);
    }

    public ExchangeGroup getLeftGroup() {
        return (ExchangeGroup) left;
    }

    public ExchangeGroup getRightGroup() {
        return (ExchangeGroup) right;
    }

    @Getter
    public static class ExchangeGroup extends ASTNode {
        private final List<ASTNode> nodes;

        public ExchangeGroup(List<ASTNode> nodes) {
            super(Type.ExchangeGroup, Location.of(nodes.getFirst().loc, nodes.getLast().loc));
            this.nodes = nodes;
        }

        @Override
        public String toString() {
            return STR."[\{loc}] \{nodes.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))}";
        }

        public int size() {
            return nodes.size();
        }
    }
}
