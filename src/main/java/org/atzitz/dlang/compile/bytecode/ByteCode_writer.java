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

@Getter
public class ByteCode_writer {
    private final ASTProgram AST;

    private final String raw;

    private final Map<String, ASTDeclareStmt> declarations;
    private final Map<String, Integer> classesLocals = new HashMap<>();

    private final ScopeVisitor scope;
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
        writer = new _Writer(filename);
        try {
            _walk(AST);
        } finally {
            writer.close();
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
    }

    private void _walkFuncDef(ASTFunctionDef nd) {
    }

    // +-------------------------+
    // |       Statements        |
    // +-------------------------+
    private void _walkReturnStmt(ASTReturnStmt nd) {
    }

    private void _walkIfStmt(ASTIfStmt nd) {
    }

    private void _walkWhileStmt(ASTWhileStmt nd) {
    }

    private void _walkFunctionCallStmt(ASTFunctionCall nd) {
    }

    private void _walkBuildCls(ASTBuildCls nd) {
    }

    private void _walkDeclareStmt(ASTDeclareStmt nd) {
    }

    private void _walkAssignStmt(ASTAssignStmt nd) {
    }

    private void _walkAttrAssignStmt(ASTAttrAssignStmt nd) {
    }

    private void _walkExchangeAssignStmt(ASTExchangeAssignStmt nd) {
    }

    // +------------------------+
    // |      Expressions       |
    // +------------------------+
    private void _walkUnOp(ASTUnaryOp nd) {
    }

    private void _walkBinOp(ASTBinaryOp nd) {
    }

    private void _walkComp(ASTComparison nd) {

    }

    private void _walkAttr(ASTAttr nd) {
        Scope last = scope.getCurrent();

        ASTAttr current = nd.base;
        do {
            writer.instruction(offset(), switch (scope.getPropertyLevel(current.attr.name)) {
                case GLOBAL -> Instructions.GLD;
                case CLASS_LEVEL -> Instructions.VLD;
                case PARAM -> Instructions.PLD;
                case LOCAL -> Instructions.LLD;
            }, scope.label(current.attr.name));
            scope.visit(declarations.get(current.attr.name).getVartype().name);

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
        writer.instruction(offset(), Instructions.BCLD, nd.value ? 1 : 0);
    }

    private void _walkIdentifier(ASTIdentifier nd) {
        switch (scope.getPropertyLevel(nd.getName())) {
            case LOCAL -> writer.instruction(offset(), Instructions.LLD, scope.label(nd.getName()));
            case PARAM -> writer.instruction(offset(), Instructions.PLD, scope.label(nd.getName()));
            case GLOBAL -> writer.instruction(offset(), Instructions.GLD, scope.label(nd.getName()));
            case CLASS_LEVEL -> writer.instruction(offset(), Instructions.VLD, scope.label(nd.getName()));
        }
    }

    private void _walkLiteral(ASTLiteral nd) {
        // TODO
    }

    private void _walkNumber(ASTNumber nd) {
        writer.instruction(offset(), Instructions.ICLD, nd.getValue());
    }

    private static class _Writer {
        private final BufferedWriter buf;

        private _Writer(String filename) {
            try {
                buf = new BufferedWriter(new FileWriter(filename));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void write(String s) {
            try {
                buf.write(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void nl() {
            write("\n");
        }

        public void close() {
            try {
                buf.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void instruction(int offset, String instr, int... args) {
            write(STR."\{offset} ");
            write(instr);
            for (int arg : args) {
                write(" ");
                write(String.valueOf(arg));
            }
            nl();
        }
    }

    public static class Instructions {
        public static final String IADD = "iadd"; // int add
        public static final String ISUB = "isub"; // int sub
        public static final String IMUL = "imul"; // int mul
        public static final String IDIV = "idiv"; // int div
        public static final String IMOD = "imod"; // int mod

        public static final String IGT = "igt"; // int greater than
        public static final String ILT = "ilt"; // int less than
        public static final String IGE = "ige"; // int greater or equal
        public static final String ILE = "ile"; // int less or equal
        public static final String INE = "ine"; // int not equal

        public static final String IINC = "iinc"; // int increment
        public static final String IDEC = "idec"; // int decrement

        public static final String LLD = "lld"; // load local
        public static final String PLD = "pld"; // load param
        public static final String VLD = "vld"; // load virtual
        public static final String RLD = "rld"; // load relative
        public static final String GLD = "gld"; // load global

        public static final String STORL = "storl"; // store local
        public static final String STORG = "storg"; // store global
        public static final String STORV = "storv"; // store virtual
        public static final String STORR = "storr"; // store relative

        public static final String ICLD = "icld"; // int constant load
        public static final String BCLD = "bcld"; // bool constant load
        public static final String RCLD = "rcld"; // reference constant load

        public static final String INVK = "invk"; // invoke
        public static final String INVKREL = "invkrel"; // invoke relative
        public static final String INVKCLS = "invkcls"; // invoke class

        public static final String JMP = "jmp"; // jump
        public static final String JMPF = "jmpf"; // jump if false
        public static final String JMPT = "jmpt"; // jump if true

        public static final String FALLOC = "falloc"; // function alloc
        public static final String FINIT = "finit"; // function init

        public static final String RET = "ret"; // return
        public static final String RETTHS = "retths"; // return this
    }
}