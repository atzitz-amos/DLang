package org.atzitz.dlang.compile.bytecode;

import lombok.Getter;
import org.atzitz.dlang.compile.bytecode.bytecodes.*;
import org.atzitz.dlang.compile.parser.Parser;
import org.atzitz.dlang.compile.parser.ScopeVisitor;
import org.atzitz.dlang.compile.parser.nodes.*;
import org.atzitz.dlang.exceptions.compile.LangCompileTimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class ByteCode {
    private final ASTProgram AST;
    private final String raw;
    private final Map<String, ASTDeclareStmt> declarations;

    private final ScopeVisitor scope;

    private List<AbstractBytecode> bytecodes;
    private int offset = 0;

    private ByteCode(Parser parser, String raw) {
        this.AST = parser.getProgram();
        this.declarations = parser.getDeclarations();
        this.scope = parser.getScope().visitor();

        this.raw = raw;
    }

    public static void main(String[] args) {
        Parser parser = new Parser("""
                int main() {
                    return 42;
                }
                int a = main();
                """);
        parser.parse();
        System.out.println(parser.getProgram());
        ByteCode bc = ByteCode.of(parser, "");
        System.out.println(bc);
    }

    public static ByteCode of(Parser parser, String raw) {
        ByteCode bc = new ByteCode(parser, raw);
        bc.generate();
        return bc;
    }

    @SafeVarargs
    private static List<AbstractBytecode> joining(Collection<AbstractBytecode>... bytecodes) {
        List<AbstractBytecode> res = new ArrayList<>();
        for (Collection<AbstractBytecode> bc : bytecodes) {
            res.addAll(bc);
        }
        return res;
    }

    private static <T> List<AbstractBytecode> joining(Collection<T> initial, Function<T, Collection<AbstractBytecode>> func) {
        List<AbstractBytecode> res = new ArrayList<>();
        for (T t : initial) {
            res.addAll(func.apply(t));
        }
        return res;
    }

    private void generate() {
        bytecodes = generate(AST);
    }

    private List<AbstractBytecode> generate(ASTNode node) {
        return switch (node.type) {
            case ASTNode.Type.Program -> generate(((ASTProgram) node).getBody());
            case ASTNode.Type.FuncDef -> generateFuncDef((ASTFunctionDef) node);
            case ASTNode.Type.ClassDef -> generateClassDef((ASTClassDef) node);

            case ASTNode.Type.WhileStmt -> generateWhileStmt((ASTWhileStmt) node);
            case ASTNode.Type.IfStmt -> generateIfStmt((ASTIfStmt) node);
            case ASTNode.Type.DeclareStmt -> generateDeclareStmt((ASTDeclareStmt) node);
            case ASTNode.Type.AssignStmt -> generateAssignStmt((ASTAssignStmt) node);
            case ASTNode.Type.AttrAssignStmt -> generateAttrAssignStmt((ASTAttrAssignStmt) node);
            case ASTNode.Type.FunctionCallStmt -> generateFunctionCall((ASTFunctionCall) node);
            case ASTNode.Type.AttrFunctionCallStmt -> generateAttrFunctionCall((ASTAttrFunctionCall) node);
            case ASTNode.Type.ReturnStmt -> generateReturnStmt((ASTReturnStmt) node);

            case ASTNode.Type.BuildCls -> generateBuildCls((ASTBuildCls) node);

            case ASTNode.Type.ExchangeAssignStmt -> generateExchangeAssignStmt((ASTExchangeAssignStmt) node);
            case ASTNode.Type.ExchangeGroup -> generate(((ASTExchangeAssignStmt.ExchangeGroup) node).getNodes());

            case ASTNode.Type.ExprStmt -> generate(((ASTExprStmt) node).getContent());

            case ASTNode.Type.BinOp -> generateBinOp((ASTBinOp) node);
            case ASTNode.Type.Comp -> generateComp((ASTComparison) node);

            case ASTNode.Type.Number -> generateNumber((ASTNumber) node);
            case ASTNode.Type.Identifier -> generateIdentifier((ASTIdentifier) node);
            case ASTNode.Type.Attr -> generateAttr((ASTAttr) node);
            case ASTNode.Type.Parameter -> null;
        };
    }

    private List<AbstractBytecode> generate(Collection<ASTNode> nodes) {
        return joining(nodes, this::generate);
    }

    private List<AbstractBytecode> generateClassDef(ASTClassDef node) {
        int pointer = scope.labelObj(node.getId().name);
        scope.visit(node.getId().name);

        int localsNum = scope.getCurrent().getLocals().size();

        int begin = offset();
        offset(); // Prepare BCFuncAlloc instruction and BCInitFunc instruction

        List<AbstractBytecode> res = generate(node.getBody());
        scope.unvisit();
        return joining(List.of(new BCFuncAlloc(offset, pointer, begin), new BCInitFunc(localsNum, begin + 1)), res);
    }

    private List<AbstractBytecode> generateFuncDef(ASTFunctionDef node) {
        int pointer = scope.labelObj(node.getName().name);
        scope.visit(node.getName().name);

        int localsNum = scope.getCurrent().getLocals().size();

        int begin = offset();
        offset(); // Prepare BCFuncAlloc and BCInitFunc instructions
        List<AbstractBytecode> res = generate(node.getBody());
        scope.unvisit();

        if (res.getLast().type != AbstractBytecode.Type.Return) {
            res.add(new BCLoadConst(0, offset()));
            res.add(new BCReturn(offset()));
        }
        return joining(List.of(new BCFuncAlloc(offset, pointer, begin), new BCInitFunc(localsNum, begin + 1)), res);
    }

    private List<AbstractBytecode> generateBuildCls(ASTBuildCls node) {
        return joining(generate(node.params), List.of(new BCBuildCls(scope.labelObj(node.cls.name), node.params.size(), offset()))); // TODO
    }

    private List<AbstractBytecode> generateFunctionCall(ASTFunctionCall node) {
        return joining(generate(node.params), List.of(new BCCallFunc(scope.labelObj(node.id.name), node.params.size(), offset())));
    }

    private List<AbstractBytecode> generateAttrFunctionCall(ASTAttrFunctionCall node) {
        int label = scope.label(node.attr.cls.name);
        scope.navigateTo(declarations.get(node.attr.cls.name).getVartype().name);
        List<AbstractBytecode> res = joining(generate(node.params), List.of(new BCInvokeFunc(label, scope.labelObj(node.attr.attr.name), node.params.size(), offset())));
        scope.revert();
        return res;
    }

    private List<AbstractBytecode> generateReturnStmt(ASTReturnStmt node) {
        return joining(generate(node.expr), List.of(new BCReturn(offset())));
    }

    private List<AbstractBytecode> generateWhileStmt(ASTWhileStmt node) {
        int begin = offset;
        List<AbstractBytecode> expr = generate(node.getExpr());
        int instrOffset = offset(); // Prepare the JumpIfFalse instruction
        Collection<AbstractBytecode> body = generate(node.getBody());
        return joining(expr, List.of(new BCJumpIfFalse(offset + 1, instrOffset)), body, List.of(new BCJump(begin, offset())));
    }

    private List<AbstractBytecode> generateIfStmt(ASTIfStmt node) {
        if (node.getExpr() == null) {
            return generate(node.getBody());
        }
        List<AbstractBytecode> expr = generate(node.getExpr());
        int instrOffset = offset(); // Prepare the JumpIfFalse instruction
        Collection<AbstractBytecode> body = generate(node.getBody());
        if (node.getAlternative() != null) {
            int instrOffset2 = offset(); // Prepare the Jump instruction
            List<AbstractBytecode> alternative = generate(node.getAlternative());
            return joining(expr, List.of(new BCJumpIfFalse(offset - alternative.size(), instrOffset)), body, List.of(new BCJump(offset, instrOffset2)), alternative);
        }
        return joining(expr, List.of(new BCJumpIfFalse(offset, instrOffset)), body);
    }

    private List<AbstractBytecode> generateDeclareStmt(ASTDeclareStmt node) {
        if (node.getInit() != null) {
            return joining(generate(node.getInit()), List.of(new BCStoreDynamic(scope.label(node.getName()
                    .getName()), offset())));
        }
        return List.of();
    }

    private List<AbstractBytecode> generateAssignStmt(ASTAssignStmt node) {
        if (node instanceof ASTExchangeAssignStmt exchangeAssignStmt) {
            return generateExchangeAssignStmt(exchangeAssignStmt);
        }
        String name = ((ASTIdentifier) node.getLeft()).name;
        List<AbstractBytecode> right = generate(node.getRight());
        AbstractBytecode bc = switch (scope.getPropertyLevel(name)) {
            case LOCAL -> new BCStoreDynamic(scope.label(name), offset());
            case PARAM -> throw new LangCompileTimeException("Cannot assign to a parameter");
            case GLOBAL -> new BCStoreGlobal(scope.label(name), offset());
            case CLASS_LEVEL -> new BCStoreAttr(-1, scope.label(name), offset());
        };
        return joining(right, List.of(bc));
    }


    private List<AbstractBytecode> generateAttrAssignStmt(ASTAttrAssignStmt node) {
        // return generateAttrAssignStmt(node.getAttr().cls.name, node.getAttr().attr.name, (ASTExprStmt) node.getRight());
        int label = scope.label(node.getAttr().cls.name);
        scope.navigateTo(declarations.get(node.getAttr().cls.name).getVartype().name);
        List<AbstractBytecode> res = joining(generate(node.getRight()), List.of(new BCStoreAttr(label, scope.label(node.getAttr().attr.name), offset())));
        scope.revert();
        return res;
    }

    private List<AbstractBytecode> generateExchangeAssignStmt(ASTExchangeAssignStmt node) {
        List<AbstractBytecode> right = generate(node.getRight());
        int[] ids = node.getLeftGroup()
                .getNodes()
                .stream()
                .map(n -> (scope.label(((ASTIdentifier) n).getName())))
                .mapToInt(Integer::intValue)
                .toArray();
        return joining(right, List.of(new BCExchangeAssign(ids, node.getRightGroup().size(), offset())));
    }

    private List<AbstractBytecode> generateBinOp(ASTBinOp node) {
        return joining(generate(node.getLeft()), generate(node.getRight()), List.of(new BCBinOp(node.getOp(), offset())));
    }

    private List<AbstractBytecode> generateComp(ASTComparison node) {
        return joining(generate(node.getLeft()), generate(node.getRight()), List.of(new BCCompare(node.getOp(), offset())));
    }

    private List<AbstractBytecode> generateNumber(ASTNumber node) {
        return List.of(new BCLoadConst(node.getValue(), offset()));
    }

    private List<AbstractBytecode> generateIdentifier(ASTIdentifier node) {
        return List.of(
                switch (scope.getPropertyLevel(node.getName())) {
                    case LOCAL -> new BCLoadDynamic(scope.label(node.getName()), offset());
                    case PARAM -> new BCLoadParam(scope.label(node.getName()), offset());
                    case GLOBAL -> new BCLoadGlobal(scope.label(node.getName()), offset());
                    case CLASS_LEVEL -> new BCLoadAttr(-1, scope.label(node.getName()), offset());
                });
    }

    private List<AbstractBytecode> generateAttr(ASTAttr node) {
        int label = scope.label(node.attr.name);
        scope.navigateTo(declarations.get(node.cls.name).getVartype().name);
        AbstractBytecode res = new BCLoadAttr(label, scope.label(node.cls.name), offset());
        scope.revert();
        return List.of(res);
    }

    private int offset() {
        return offset++;
    }

    @Override
    public String toString() {
        return STR."""
                [
                    \{bytecodes.stream().map(Object::toString).collect(Collectors.joining("\n\t"))}
                ]
                """;
    }
}
