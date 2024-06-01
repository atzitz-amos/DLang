package org.atzitz.dlang.compile.bytecode;

import lombok.Getter;
import org.atzitz.dlang.compile.parser.Parser;
import org.atzitz.dlang.compile.parser.Scope;
import org.atzitz.dlang.compile.parser.ScopeVisitor;
import org.atzitz.dlang.compile.parser.nodes.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ByteCode_writer {
    private final ASTProgram AST;

    private final String raw;

    private final Map<String, ASTDeclareStmt> declarations;
    private final Map<String, Integer> classesLocals = new HashMap<>();

    private final ScopeVisitor scope;

    private String lastInstruction = "";
    private int offset = 0;

    private _Writer writer;

    private ByteCode_writer(Parser parser, String raw) {
        this.AST = parser.getProgram();
        this.declarations = parser.getDeclarations();
        this.scope = parser.getScope().visitor();

        this.raw = raw;
    }

    public static void main(String[] args) {
        Parser parser = new Parser("""
                int fib(int n) {
                    if (n <= 1) {
                        return n;
                    }
                    return fib(n - 1) + fib(n - 2);
                }
                                
                int x = fib(2);
                """);
        parser.parse();
        System.out.println(parser.getProgram());
        ByteCode_writer bc = new ByteCode_writer(parser, "");
        bc.generate("out.txt");
    }

    // +-----------------------+
    // |       Utilities       |
    // +-----------------------+
    private int offset() {
        return offset++;
    }

    // +-------------------------+
    // |       Generating        |
    // +-------------------------+
    public void generate(String filename) {
        writer = new _Writer(this);
        try {
            _walk(AST);
            writer.writeToFile(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void _walk(ASTNode nd) {
        switch (nd.type) {
            case Program -> ((ASTProgram) nd).getBody().forEach(this::_walk);

            case ClassDef -> _walkClassDef((ASTClassDef) nd);
            case FuncDef -> _walkFuncDef((ASTFunctionDef) nd);
            case Parameter -> {
            }

            case ReturnStmt -> _walkReturnStmt((ASTReturnStmt) nd);
            case IfStmt -> _walkIfStmt((ASTIfStmt) nd);
            case WhileStmt -> _walkWhileStmt((ASTWhileStmt) nd);

            case FunctionCallStmt -> _walkFunctionCallStmt((ASTFunctionCall) nd);
            case BuildCls -> _walkBuildCls((ASTBuildCls) nd);

            case DeclareStmt -> _walkDeclareStmt((ASTDeclareStmt) nd);
            case AssignStmt -> _walkAssignStmt((ASTAssignStmt) nd);
            case AttrAssignStmt -> _walkAttrAssignStmt((ASTAttrAssignStmt) nd);
            case ExchangeAssignStmt -> _walkExchangeAssignStmt((ASTExchangeAssignStmt) nd);
            case ExchangeGroup -> ((ASTExchangeAssignStmt.ExchangeGroup) nd).getNodes().forEach(this::_walk);

            case ExprStmt -> _walk(((ASTExprStmt) nd).getContent());
            case UnOp -> _walkUnOp((ASTUnaryOp) nd);
            case BinOp -> _walkBinOp((ASTBinaryOp) nd);
            case Comparison -> _walkComp((ASTComparison) nd);

            case Attr -> _walkAttr((ASTAttr) nd);
            case Array -> _walkArray((ASTArray) nd);
            case ArrayAccess -> _walkArrayAccess((ASTArrayAccess) nd);
            case Boolean -> _walkBoolean((ASTBoolean) nd);
            case Identifier -> _walkIdentifier((ASTIdentifier) nd);
            case Literal -> _walkLiteral((ASTLiteral) nd);
            case Number -> _walkNumber((ASTNumber) nd);
        }
    }

    // +------------------------+
    // |      Definitions       |
    // +------------------------+
    private void _walkClassDef(ASTClassDef nd) {
        int pointer = scope.labelObj(nd.getId().name);
        scope.visit(nd.getId().name);

        classesLocals.put(nd.getId().name, scope.getCurrent().getLocals().size() + scope.getCurrent()
                .getLocalObjects()
                .size());

        int begin = offset();

        nd.getBody().forEach(this::_walk);
        scope.unvisit();

        writer.instruction(begin, Instruction.FALLOC, STR."<class '\{nd.getId().name}'>", pointer, offset + 2);
        writer.instruction(offset(), Instruction.RETTHS);
    }

    private void _walkFuncDef(ASTFunctionDef nd) {
        scope.visit(nd.getName().name);

        int localsNum = scope.getCurrent().getLocals().size() + scope.getCurrent().getLocalObjects().size();

        int begin = offset();
        offset(); // Prepare BCFuncAlloc and BCInitFunc instructions

        nd.getBody().forEach(this::_walk);
        scope.unvisit();


        if (!Objects.equals(lastInstruction, Instruction.RET)) {
            writer.instruction(offset(), Instruction.ICLD, 0);
            writer.instruction(offset(), Instruction.RET);
        }

        writer.instruction(begin, Instruction.FALLOC, STR."<func '\{nd.name.name}'>", scope.labelObj(nd.getName().name), offset);
        writer.instruction(begin + 1, Instruction.FINIT, localsNum);
    }

    // +-------------------------+
    // |       Statements        |
    // +-------------------------+
    private void _walkReturnStmt(ASTReturnStmt nd) {
        _walk(nd.expr);
        writer.instruction(offset(), Instruction.RET);
    }

    private void _walkIfStmt(ASTIfStmt nd) {
        if (nd.getExpr() == null) {
            nd.getBody().forEach(this::_walk);
            return;
        }

        _walk(nd.getExpr());
        int instrOffset = offset(); // Prepare the JumpIfFalse instruction
        nd.getBody().forEach(this::_walk);

        if (nd.getAlternative() != null) {
            int instrOffset2 = offset(); // Prepare the Jump instruction
            _walk(nd.getAlternative());
            writer.instruction(instrOffset, Instruction.JMPF, offset - instrOffset2);
            writer.instruction(instrOffset2, Instruction.JMP, offset);
            return;
        }
        writer.instruction(instrOffset, Instruction.JMPF, offset);
    }

    private void _walkWhileStmt(ASTWhileStmt nd) {
        int begin = offset;
        _walk(nd.getExpr());
        int instrOffset = offset(); // Prepare the JumpIfFalse instruction

        nd.getBody().forEach(this::_walk);
        writer.instruction(instrOffset, Instruction.JMPF, offset + 1);
        writer.instruction(offset(), Instruction.JMP, begin);
    }

    private void _walkFunctionCallStmt(ASTFunctionCall nd) {
        if (nd.id instanceof ASTAttr attr) {
            if (attr.parent == null) {
                nd.params.forEach(this::_walk);
                writer.instruction(offset(), Instruction.INVK, scope.label(attr.attr.getName()), nd.params.size());
            } else {
                nd.params.forEach(this::_walk);
                _walkAttr(attr);
                writer.instruction(offset(), Instruction.INVKREL, nd.params.size());
            }
        } else if (nd.id instanceof ASTIdentifier identifier) {
            nd.params.forEach(this::_walk);
            writer.instruction(offset(), Instruction.INVK, scope.label(identifier.getName()), nd.params.size());
        } else throw new RuntimeException("Invalid function call");
    }

    private void _walkBuildCls(ASTBuildCls nd) {
        nd.params.forEach(this::_walk);
        writer.instruction(offset(), Instruction.INVKCLS, scope.labelObj(nd.cls.name), nd.params.size(), classesLocals.get(nd.cls.name));
    }

    private void _walkDeclareStmt(ASTDeclareStmt nd) {
        if (nd.getInit() != null) {
            _walk(nd.getInit());
            writer.instruction(offset(), switch (scope.getPropertyLevel(nd.getName().name)) {
                case LOCAL -> Instruction.STORL;
                case GLOBAL -> Instruction.STORG;
                case CLASS_LEVEL -> Instruction.STORV;
                case PARAM -> throw new RuntimeException("Cannot assign to a parameter");
            }, STR."<var '\{nd.getName().name}'>", scope.label(nd.getName().name));
        }
    }

    private void _walkAssignStmt(ASTAssignStmt nd) {
        String name = ((ASTIdentifier) nd.getLeft()).getName();
        _walk(nd.getRight());
        writer.instruction(offset(), switch (scope.getPropertyLevel(name)) {
            case LOCAL -> Instruction.STORL;
            case GLOBAL -> Instruction.STORG;
            case CLASS_LEVEL -> Instruction.STORV;
            case PARAM -> throw new RuntimeException("Cannot assign to a parameter");
        }, STR."<var '\{((ASTIdentifier) nd.getLeft()).name}'>", scope.label(name));
    }

    private void _walkAttrAssignStmt(ASTAttrAssignStmt nd) {
        // TODO
    }

    private void _walkExchangeAssignStmt(ASTExchangeAssignStmt nd) {
        // TODO
    }

    // +------------------------+
    // |      Expressions       |
    // +------------------------+
    private void _walkUnOp(ASTUnaryOp nd) {
        _walk(nd.getNode());
        writer.instruction(offset(), switch (nd.getOp()) {
            case "-" -> Instruction.INEG;
            case "!" -> Instruction.BNEG;
            default -> throw new RuntimeException(STR."No known unary operator: \{nd.getOp()}");
        });
    }

    private void _walkBinOp(ASTBinaryOp nd) {
        _walk(nd.getLeft());
        _walk(nd.getRight());
        writer.instruction(offset(), switch (nd.getOp()) {
            case "+" -> Instruction.IADD;
            case "-" -> Instruction.ISUB;
            case "*" -> Instruction.IMUL;
            case "/" -> Instruction.IDIV;
            case "%" -> Instruction.IMOD;
            default -> throw new RuntimeException(STR."No known binary operator: \{nd.getOp()}");
        });
    }

    private void _walkComp(ASTComparison nd) {
        _walk(nd.getRight());
        _walk(nd.getLeft());
        writer.instruction(offset(), switch (nd.getOp()) {
            case "==" -> Instruction.IEQ;
            case "!=" -> Instruction.INE;
            case "<" -> Instruction.ILT;
            case "<=" -> Instruction.ILE;
            case ">" -> Instruction.IGT;
            case ">=" -> Instruction.IGE;
            default -> throw new RuntimeException(STR."No known comparison operator: \{nd.getOp()}");
        });
    }

    private void _walkAttr(ASTAttr nd) {
        Scope last = scope.getCurrent();

        ASTAttr current = nd.base;
        do {
            writer.instruction(offset(), switch (scope.getPropertyLevel(current.attr.name)) {
                case GLOBAL -> Instruction.GLD;
                case CLASS_LEVEL -> Instruction.VLD;
                case PARAM -> Instruction.PLD;
                case LOCAL -> Instruction.LLD;
            }, STR."<var '\{current.attr.name}'>", scope.label(current.attr.name));
            // scope.visit(declarations.get(current.attr.name).getVartype().name);

            current = nd.child;
        } while (current != null);


        scope.setCurrent(last);
    }

    private void _walkArrayAccess(ASTArrayAccess nd) {
        // TODO
    }

    private void _walkArray(ASTArray nd) {
        // TODO
    }

    private void _walkBoolean(ASTBoolean nd) {
        writer.instruction(offset(), Instruction.BCLD, nd.value ? 1 : 0);
    }

    private void _walkIdentifier(ASTIdentifier nd) {
        switch (scope.getPropertyLevel(nd.getName())) {
            case LOCAL ->
                    writer.instruction(offset(), Instruction.LLD, STR."<var '\{nd.name}'>", scope.label(nd.getName()));
            case PARAM ->
                    writer.instruction(offset(), Instruction.PLD, STR."<var '\{nd.name}'>", scope.label(nd.getName()));
            case GLOBAL ->
                    writer.instruction(offset(), Instruction.GLD, STR."<var '\{nd.name}'>", scope.label(nd.getName()));
            case CLASS_LEVEL -> writer.instruction(offset(), Instruction.VLD, scope.label(nd.getName()));
        }
    }

    private void _walkLiteral(ASTLiteral nd) {
        // TODO
    }

    private void _walkNumber(ASTNumber nd) {
        writer.instruction(offset(), Instruction.ICLD, nd.getValue());
    }

    public enum Instruction {
        IADD("iadd"), // int add
        ISUB("isub"), // int sub
        IMUL("imul"), // int mul
        IDIV("idiv"), // int div
        IMOD("imod"), // int mod

        INEG("ineg"), // int neg (-a)
        BNEG("bneg"), // boolean neg (!a)

        IGT("igt"), // int greater than
        ILT("ilt"), // int less than
        IGE("ige"), // int greater or equal
        ILE("ile"), // int less or equal
        INE("ine"), // int not equal
        IEQ("ieq"), // int not equal

        IINC("iinc"), // int increment
        IDEC("idec"), // int decrement

        LLD("lld", true), // load local
        PLD("pld", true), // load param
        VLD("vld", true), // load virtual
        RLD("rld", true), // load relative
        GLD("gld", true), // load global

        STORL("storl", true), // store local
        STORG("storg", true), // store global
        STORV("storv", true), // store virtual
        STORR("storr", true), // store relative

        ICLD("icld"), // int constant load
        BCLD("bcld"), // bool constant load
        RCLD("rcld"), // reference constant load

        INVK("invk", true), // invoke
        INVKREL("invkrel", true), // invoke relative
        INVKCLS("invkcls", true), // invoke class

        JMP("jmp"), // jump
        JMPF("jmpf"), // jump if false
        JMPT("jmpt"), // jump if true

        FALLOC("falloc", true), // function alloc
        FINIT("finit"), // function init

        RET("ret"), // return
        RETTHS("retths"); // return this

        private final String value;
        private final boolean takesPointer;

        Instruction(String value, boolean takesPointer) {
            this.value = value;
            this.takesPointer = takesPointer;
        }

        Instruction(String value) {
            this(value, false);
        }

    }

    private static class _Writer {
        private final ByteCode_writer bc;
        private final Map<Integer, String> instructions = new HashMap<>();
        private final Map<Integer, String> comments = new HashMap<>();

        private _Writer(ByteCode_writer bc) {
            this.bc = bc;
        }

        public void instruction(int offset, Instruction instr, int... args) {
            instruction(offset, instr, "", args);
        }

        public void instruction(int offset, Instruction instr, String comment, int... args) {
            StringBuilder sb = new StringBuilder();
            sb.append(offset).append(": ");
            sb.append(instr.value);
            boolean firstParam = true;
            for (int arg : args) {
                if (firstParam && instr.takesPointer) sb.append(" #").append(arg);
                else sb.append(" ").append(arg);
                firstParam = false;
            }
            instructions.put(offset, sb.toString());
            comments.put(offset, comment);

            bc.lastInstruction = instr.value;
        }

        public void writeToFile(String filename) throws IOException {
            new FileWriter(filename, false).close();  // Clear the file

            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

            writer.write("nglobals=");
            writer.write(STR."\{bc.scope.getRoot().getLocalObjects().size()}\n");

            // Calculate the maximum length of the instruction strings
            int maxInstructionLength = instructions.values().stream().mapToInt(String::length).max().orElse(0);

            instructions.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
                try {
                    String value = e.getValue();
                    String comment = comments.get(e.getKey());

                    int d = (int) (-value.indexOf(':') + Math.floor(Math.log(instructions.size())));
                    value = value.substring(0, value.indexOf(':') + 1) + " ".repeat(d) + value.substring(value.indexOf(':') + 1);

                    if (comment != null && !comment.isEmpty()) {
                        String paddedValue = String.format(STR."%-\{maxInstructionLength}s", value);

                        writer.write(STR."\{paddedValue}\t// \{comment}");
                    } else writer.write(value);
                    writer.newLine();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            writer.close();
        }

    }
}