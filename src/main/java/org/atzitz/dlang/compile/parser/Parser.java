package org.atzitz.dlang.compile.parser;

import lombok.Getter;
import org.atzitz.dlang.compile.Constants;
import org.atzitz.dlang.compile.Location;
import org.atzitz.dlang.compile.lexer.Token;
import org.atzitz.dlang.compile.lexer.TokenType;
import org.atzitz.dlang.compile.lexer.Tokenizer;
import org.atzitz.dlang.compile.parser.nodes.*;
import org.atzitz.dlang.exceptions.compile.LangCompileTimeException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Parser {
    public final Tokenizer tokenizer;

    private final @Getter Map<String, ASTDeclareStmt> declarations = new HashMap<>();
    private final @Getter ScopeVisitor scope = new ScopeVisitor();
    private int internalScopeCounter = 0;

    private @Getter ASTProgram program;


    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Parser(String raw) {
        this.tokenizer = new Tokenizer(raw);
    }

    public static void main(String[] args) {
        Parser parser = new Parser("""
                a.b.c[d]
                """);
        parser.parse();
        System.out.println(parser.getProgram());

        ScopeVisitor visitor = parser.scope.visitor();
        System.out.println(STR."cls = \{visitor.label("cls")}#\{visitor.getPropertyLevel("cls")}");
        System.out.println(STR."a = \{visitor.label("a")}#\{visitor.getPropertyLevel("a")}");
        System.out.println(STR."y = \{visitor.label("y")}#\{visitor.getPropertyLevel("y")}");
        visitor.visit("MyClass");
        System.out.println("-----------------");
        System.out.println(STR."attr = \{visitor.label("attr")}#\{visitor.getPropertyLevel("attr")}");
        System.out.println(STR."cls = \{visitor.label("cls")}#\{visitor.getPropertyLevel("cls")}");
        System.out.println(STR."a = \{visitor.label("a")}#\{visitor.getPropertyLevel("a")}");
        visitor.visit("add");
        System.out.println("-----------------");
        System.out.println(STR."a = \{visitor.label("a")}#\{visitor.getPropertyLevel("a")}");
        System.out.println(STR."b = \{visitor.label("b")}#\{visitor.getPropertyLevel("b")}");
        System.out.println(STR."attr = \{visitor.label("attr")}#\{visitor.getPropertyLevel("attr")}");
        System.out.println(STR."y = \{visitor.label("y")}#\{visitor.getPropertyLevel("y")}");
    }

    public void parse() {
        program = new ASTProgram(parseBlockStatements(), tokenizer.empty() ? Location.of(0, 0, 0, 0) : Location.of(tokenizer.getAt(0)
                .loc(), tokenizer.getLast().loc()));
    }

    // -----------------------------------------------
    // |                  UTILITIES                  |
    // -----------------------------------------------
    private Token seek(int d) {
        return tokenizer.lookAhead(d);
    }

    private Token seek() {
        return seek(1);
    }

    private Token seekNext() {
        return seek(2);
    }

    private @NotNull Token eat(TokenType type) {
        Token token = tokenizer.consume();
        if (token.type() != type) {
            if (token.type() == TokenType.SemiColon) return eat(type);
            throw new LangCompileTimeException(STR."Expected '\{type}' but received instead '\{token.type()}'");
        }
        return token;
    }

    private boolean consumeIf(TokenType type) {
        if (seek().type() == type) {
            eat(type);
            return true;
        }
        return false;
    }

    private ASTIdentifier asIdentifier(Token identifier) {
        return new ASTIdentifier(identifier.value(), identifier.loc());
    }

    private ASTNode requireLineEnd(ASTNode nd) {
        if (seek().type() != TokenType.SemiColon) {
            throw new LangCompileTimeException("Missing semi-colon");
        }
        eat(TokenType.SemiColon);
        return nd;
    }


    // -----------------------------------------------
    // |                   PARSING                   |
    // -----------------------------------------------
    private Collection<ASTNode> parseBlockStatements() {
        Collection<ASTNode> result = new ArrayList<>();

        while (true) {
            Token token = seek();
            if (token.type() == TokenType.EOF) {
                if (!scope.isGlobal()) throw new LangCompileTimeException("Unexpected EOF");
                break;
            } else if (consumeIf(TokenType.CloseBrace)) {
                if (scope.isGlobal()) throw new LangCompileTimeException("Unexpected '}'");
                scope.close();
                break;
            } else {
                ASTNode node = parseStmt();
                result.add(node);
            }
        }
        return result;
    }

    // STATEMENTS
    private ASTNode parseStmt() {
        Token token = seek();
        if (token.type() == TokenType.Keyword) {
            eat(TokenType.Keyword);
            if (Objects.equals(token.value(), Constants.KEYWORDS.IF)) {
                return parseIfStmts();
            } else if (Objects.equals(token.value(), Constants.KEYWORDS.ELSE)) {
                throw new LangCompileTimeException("Else outside of if statement");
            } else if (Objects.equals(token.value(), Constants.KEYWORDS.WHILE)) {
                return parseWhileStmt();
            } else if (Objects.equals(token.value(), Constants.KEYWORDS.CLASS)) {
                return parseClassDefStmt();
            } else if (Objects.equals(token.value(), Constants.KEYWORDS.RETURN)) {
                return requireLineEnd(new ASTReturnStmt(parseExpr(), Location.of(token.loc(), tokenizer.lookBehind()
                        .loc())));
            } else {
                throw new LangCompileTimeException(STR."Unexpected keyword '\{token.value()}'");
            }
        } else if (token.type() == TokenType.Identifier) {
            if (seekNext().type() == TokenType.Identifier) {
                if (seek(3).type() == TokenType.OpenParen) return parseFunctionDefStmt();
                return requireLineEnd(parseDeclarationStmt());
            } else if (seekNext().type() == TokenType.Equals) {
                return requireLineEnd(parseAssignmentStmt());
            } else if (seekNext().type() == TokenType.OpEquals) {
                return requireLineEnd(parseOperatorAssignmentStmt());
            } else if (seekNext().type() == TokenType.Comma) {
                return requireLineEnd(parseExchangeAssignmentStmt());
            }

        }
        return parseExpr();
    }

    private ASTNode parseFunctionDefStmt() {
        Token type = eat(TokenType.Identifier);
        ASTIdentifier id = asIdentifier(eat(TokenType.Identifier));

        scope.addLocalObject(id.name);
        scope.open(Scope.Type.FUNCTION, id.name);

        eat(TokenType.OpenParen);
        List<ASTParameter> args = new ArrayList<>();
        while (seek().type() != TokenType.CloseParen) {
            ASTIdentifier tp = asIdentifier(eat(TokenType.Identifier));
            ASTIdentifier name = asIdentifier(eat(TokenType.Identifier));
            args.add(new ASTParameter(tp, name));
            scope.addParam(name.name);
            if (seek().type() != TokenType.Comma) break;
            eat(TokenType.Comma);
        }
        eat(TokenType.CloseParen);
        eat(TokenType.OpenBrace);

        return new ASTFunctionDef(asIdentifier(type), id, args, parseBlockStatements(), Location.of(type.loc(), tokenizer.lookBehind()
                .loc()));
    }

    private ASTNode parseClassDefStmt() {
        ASTIdentifier id = asIdentifier(eat(TokenType.Identifier));
        eat(TokenType.OpenBrace);

        scope.addLocalObject(id.name);
        scope.open(Scope.Type.CLASS, id.name);
        return new ASTClassDef(id, parseBlockStatements(), Location.of(tokenizer.lookBehind()
                .loc(), tokenizer.lookBehind().loc()));
    }

    private ASTNode parseAssignmentStmt() {
        ASTIdentifier id = asIdentifier(eat(TokenType.Identifier));

        eat(TokenType.Equals);
        return new ASTAssignStmt(id, "=", parseExpr(), Location.of(id.loc, tokenizer.lookBehind()
                .loc()));
    }

    private ASTNode parseDeclarationStmt() {
        Token type = eat(TokenType.Identifier);
        ASTIdentifier id = asIdentifier(eat(TokenType.Identifier));

        if (scope.exists(id.name)) {
            throw new LangCompileTimeException(STR."Variable '\{id.name}' cannot be redeclared");
        }

        scope.addLocal(id.name);

        ASTDeclareStmt result;
        if (consumeIf(TokenType.Equals)) {
            result = new ASTDeclareStmt(asIdentifier(type), id, parseExpr(), Location.of(type.loc(), tokenizer.lookBehind()
                    .loc()));
        } else
            result = new ASTDeclareStmt(asIdentifier(type), id, null, Location.of(type.loc(), id.loc));
        declarations.put(id.name, result);
        return result;
    }

    private ASTNode parseOperatorAssignmentStmt() {
        Token id = eat(TokenType.Identifier);
        if (!declarations.containsKey(id.value())) {
            throw new LangCompileTimeException(STR."Variable '\{id.value()}' has not been declared");
        }

        return new ASTAssignStmt(asIdentifier(id), eat(TokenType.OpEquals).value(), parseExpr(), Location.of(id.loc(), tokenizer.lookBehind()
                .loc()));
    }

    private ASTNode parseExchangeAssignmentStmt() {
        List<ASTNode> selectors = new ArrayList<>();
        List<ASTNode> values = new ArrayList<>();

        selectors.add(asIdentifier(eat(TokenType.Identifier)));
        while (seek().type() == TokenType.Comma) {
            eat(TokenType.Comma);
            selectors.add(asIdentifier(eat(TokenType.Identifier)));
        }

        eat(TokenType.Equals);

        values.add(parseExpr());
        while (seek().type() == TokenType.Comma) {
            eat(TokenType.Comma);
            values.add(parseExpr());
        }

        if (selectors.size() != values.size()) {
            throw new LangCompileTimeException("Cannot perform exchange assignment with different number of selectors and values");
        }
        return new ASTExchangeAssignStmt(selectors, values, Location.of(selectors.getFirst().loc, tokenizer.lookBehind()
                .loc()));
    }

    private ASTNode parseWhileStmt() {
        Token begin = tokenizer.lookBehind();
        eat(TokenType.OpenParen);
        ASTExprStmt expr = (ASTExprStmt) parseExpr();
        eat(TokenType.CloseParen);
        eat(TokenType.OpenBrace);

        scope.open(Scope.Type.INTERNAL, STR."while$\{internalScopeCounter++}");
        Collection<ASTNode> block = parseBlockStatements();

        return new ASTWhileStmt(expr, block, Location.of(begin.loc(), tokenizer.lookBehind()
                .loc()));
    }

    private ASTIfStmt parseIfStmt() {
        Token begin = tokenizer.lookBehind();
        eat(TokenType.OpenParen);
        ASTExprStmt expr = (ASTExprStmt) parseExpr();
        eat(TokenType.CloseParen);
        eat(TokenType.OpenBrace);

        scope.open(Scope.Type.INTERNAL, STR."if$\{internalScopeCounter++}");
        return new ASTIfStmt(expr, parseBlockStatements(), Location.of(begin.loc(), tokenizer.lookBehind().loc()));
    }

    private ASTNode parseIfStmts() {
        ASTIfStmt stmt = parseIfStmt();
        ASTIfStmt base = stmt;

        while (seek().type() == TokenType.Keyword) {
            if (seek().value().equals(Constants.KEYWORDS.ELSE)) {
                eat(TokenType.Keyword);
                if (seek().value().equals(Constants.KEYWORDS.IF)) {
                    eat(TokenType.Keyword);
                    stmt = stmt.alternative(parseIfStmt());
                } else {
                    stmt = stmt.alternative(parseElseStmt());
                }
            } else {
                break;
            }
        }
        return base;
    }

    private ASTIfStmt parseElseStmt() {
        Token begin = eat(TokenType.OpenBrace);
        scope.open(Scope.Type.INTERNAL, STR."if$\{internalScopeCounter++}");
        Collection<ASTNode> block = parseBlockStatements();
        return new ASTIfStmt(null, block, Location.of(begin.loc(), tokenizer.lookBehind().loc()));
    }

    // EXPRESSIONS

    private ASTNode parseFunctionCall(ASTIdentifier id) {
        return new ASTFunctionCall(id, parseParams(), Location.of(id.loc, tokenizer.lookBehind().loc()));
    }

    private List<ASTNode> parseParams() {
        eat(TokenType.OpenParen);
        List<ASTNode> args = new ArrayList<>();
        while (seek().type() != TokenType.CloseParen) {
            args.add(parseExpr());
            if (seek().type() != TokenType.Comma) break;
            eat(TokenType.Comma);
        }
        eat(TokenType.CloseParen);
        return args;
    }

    private ASTNode parseExpr() {
        Token begin = seek();
        ASTNode node = parseMathExpr();
        while (seek().type() == TokenType.Comparison) {
            Token tk = seek();
            eat(TokenType.Comparison);
            node = new ASTComparison(node, tk.value(), parseMathExpr(), Location.of(begin.loc(), tokenizer.lookBehind()
                    .loc()));
        }

        return new ASTExprStmt(node);
    }

    private ASTNode parseMathExpr() {
        Token begin = seek();
        ASTNode node = parseTerm();
        while (seek().type() == TokenType.Operator && (seek().value().equals(Constants.OPERATORS.PLUS) || seek().value()
                .equals(Constants.OPERATORS.MINUS))) {
            node = new ASTBinaryOp(node, eat(TokenType.Operator).value(), parseTerm(), Location.of(begin.loc(), tokenizer.lookBehind()
                    .loc()));
            begin = seek();
        }

        return node;
    }

    private ASTNode parseTerm() {
        Token begin = seek();
        ASTNode node = parseFactor();

        while (seek().type() == TokenType.Operator && (seek().value()
                .equals(Constants.OPERATORS.MULTIPLY) || seek().value()
                .equals(Constants.OPERATORS.DIVIDE)) || seek().value().equals(Constants.OPERATORS.MODULO)) {
            node = new ASTBinaryOp(node, eat(TokenType.Operator).value(), parseFactor(), Location.of(begin.loc(), tokenizer.lookBehind()
                    .loc()));
            begin = seek();
        }

        return node;
    }

    private ASTNode parseFactor() {
        Token tk = seek();
        if (tk.type() == TokenType.Number) {
            return new ASTNumber(Integer.parseInt(tk.value()), tk.value(), eat(TokenType.Number).loc());
        } else if (tk.type() == TokenType.OpenParen) {
            eat(TokenType.OpenParen);
            ASTExprStmt expr = (ASTExprStmt) this.parseExpr();
            eat(TokenType.CloseParen);
            return expr;
        } else {
            return parseIdentifierGroup();
        }
    }

    private ASTNode parseArray() {
        Token begin = eat(TokenType.OpenBracket);
        List<ASTNode> elements = new ArrayList<>();
        while (seek().type() != TokenType.CloseBracket) {
            if (seek().type() == TokenType.EOF) {
                throw new LangCompileTimeException("Unexpected EOF in array declaration");
            }
            elements.add(parseExpr());
            if (!consumeIf(TokenType.Comma)) {
                break;
            }
        }
        return new ASTArray(elements, Location.of(begin.loc(), eat(TokenType.CloseBracket).loc()));
    }

    private ASTNode parseIdentifierGroup() {
        Token tok = seek();
        if (tok.type() == TokenType.ExclamationMark) {
            eat(TokenType.ExclamationMark);
            return new ASTUnaryOp("!", parseIdentifierGroup(), Location.of(tok.loc(), tokenizer.lookBehind().loc()));
        } else if (tok.type() == TokenType.Operator && Objects.equals(tok.value(), "-")) {
            eat(TokenType.Operator);
            return new ASTUnaryOp("-", parseIdentifierGroup(), Location.of(tok.loc(), tokenizer.lookBehind().loc()));
        } else if (tok.type() == TokenType.OpenBracket) {
            return parseArray();
        } else if (tok.type() == TokenType.Keyword) {
            eat(TokenType.Keyword);
            if (tok.value().equals(Constants.KEYWORDS.NEW)) {
                return new ASTBuildCls(asIdentifier(eat(TokenType.Identifier)), parseParams(), Location.of(tok.loc(), tokenizer.lookBehind()
                        .loc()));
            } else if (tok.value().equals(Constants.KEYWORDS.TRUE)) {
                return new ASTBoolean(true, Location.of(tok.loc()));
            } else if (tok.value().equals(Constants.KEYWORDS.FALSE)) {
                return new ASTBoolean(false, Location.of(tok.loc()));
            } else throw new LangCompileTimeException(STR."Unexpected keyword '\{tok.value()}'");
        } else if (tok.type() == TokenType.Identifier || tok.type() == TokenType.Number) {
            ASTAttr base = ASTAttr.asRoot(asIdentifier(eat(seek().type())));
            while (seek().type() == TokenType.Dot) {
                eat(TokenType.Dot);
                base = new ASTAttr(base, asIdentifier(eat(TokenType.Identifier)), Location.of(base.loc, tokenizer.lookBehind()
                        .loc()));
            }
            ASTNode result = base;
            while (seek().type() == TokenType.OpenBracket || seek().type() == TokenType.OpenParen) {
                result = parseSubscription(result);
            }

            return result;
        } else if (tok.type() == TokenType.Literal) {
            return parseLiteral();
        } else {
            throw new LangCompileTimeException(STR."Unexpected token '\{tok.value()}'");
        }
    }

    private ASTNode parseSubscription(ASTNode base) {
        if (seek().type() == TokenType.OpenParen) {
            eat(TokenType.OpenParen);
            List<ASTNode> params = new ArrayList<>();
            while (seek().type() != TokenType.CloseParen) {
                if (seek().type() == TokenType.EOF) {
                    throw new LangCompileTimeException("Unexpected EOF in function call");
                }
                params.add(parseExpr());
                if (!consumeIf(TokenType.Comma)) {
                    break;
                }
            }
            eat(TokenType.CloseParen);
            return new ASTFunctionCall(base, params, Location.of(base.loc, tokenizer.lookBehind().loc()));
        } else if (seek().type() == TokenType.OpenBracket) {
            eat(TokenType.OpenBracket);
            ASTNode index = parseExpr();
            eat(TokenType.CloseBracket);
            return new ASTArrayAccess(base, index, Location.of(base.loc, tokenizer.lookBehind().loc()));
        }
        return base;
    }

    @NotNull
    private ASTLiteral parseLiteral() {
        Token tok = eat(TokenType.Literal);
        return new ASTLiteral(tok.value(), tok.loc());
    }
}
