package org.atzitz.dlang.compile.lexer;

import org.atzitz.dlang.compile.Constants;
import org.atzitz.dlang.compile.Location;
import org.atzitz.dlang.exceptions.compile.LangCompileTimeException;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Tokenizer {
    private final Source src;

    private final ArrayList<Token> tokens = new ArrayList<>();
    private int index = 0;

    public Tokenizer(String s) {
        src = new Source(s);
    }

    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer("""
                class MyClass {
                    int attr = 5;
                    
                    int getAttr() {
                        return attr;
                    }
                    
                    int add(int a, int b) {
                        return a + b;
                    }
                }
                                
                MyClass cls = new MyClass();
                int a = cls.getAttr();
                int b = cls.add(5, 6);
                """);
        while (true) {
            Token token = tokenizer.consume();
            System.out.println(token);
            if (token.type() == TokenType.EOF) break;
        }
    }

    private Token resolveIdentifiers() {
        if (!src.cacheEmpty()) {
            String cache = src.clearCache();
            Location loc = Location.of(src.cacheRow(), src.cacheCol(), src.crow(), src.ccol());
            if (Constants.KEYWORDS.contains(cache)) {
                return new Token(cache, TokenType.Keyword, loc);
            } else {
                return new Token(cache, TokenType.Identifier, loc);
            }
        }
        return null;
    }

    private String collect(Predicate<Character> predicate) {
        StringBuilder result = new StringBuilder();
        while (!src.empty() && predicate.test(src.seek())) {
            result.append(src.consume());
        }
        return result.toString();
    }

    private Token buildNext() {
        Token result = null;

        while (result == null) {
            if (src.empty())
                return new Token("", TokenType.EOF, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol()));
            char c = src.seek();
            if (c == '\n' || c == ' ') {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
            } else if (c == '=') {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                String q = collect(x -> x == '=');
                if (q.length() == 1)
                    return new Token("=", TokenType.Equals, Location.of(src.crow(), src.ccol() - 1, src.crow(), src.ccol()));
                else if (q.length() == 2)
                    return new Token("==", TokenType.Comparison, Location.of(src.crow(), src.ccol() - 2, src.crow(), src.ccol()));
                else throw new LangCompileTimeException("Unexpected '=' char");
            } else if (c == '>' || c == '<') {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                if (src.seekNext() == '=') {
                    src.consume();
                    result = new Token(STR."\{c}=", TokenType.Comparison, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + 2));
                } else {
                    result = new Token(String.valueOf(c), TokenType.Comparison, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + 1));
                }
            } else if (Constants.OPERATORS.contains(String.valueOf(c))) {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                if (src.seekNext() == '=') {
                    result = new Token(STR."\{c}=", TokenType.OpEquals, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + 2));
                    src.consume();
                } else
                    result = new Token(String.valueOf(c), TokenType.Operator, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + 1));
            } else if (Character.isDigit(c) && src.cacheEmpty()) {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                String num = collect(Character::isDigit);
                return new Token(num, TokenType.Number, Location.of(src.crow(), src.ccol() - num.length(), src.crow(), src.ccol()));
            } else if (c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']') {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                result = new Token(String.valueOf(c), switch (c) {
                    case '(' -> TokenType.OpenParen;
                    case ')' -> TokenType.CloseParen;
                    case '{' -> TokenType.OpenBrace;
                    case '}' -> TokenType.CloseBrace;
                    case '[' -> TokenType.OpenBracket;
                    case ']' -> TokenType.CloseBracket;
                    default -> throw new LangCompileTimeException("Unexpected character");
                }, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + 1));
            } else if (c == ',' || c == ';' || c == '.' || c == '!') {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                result = new Token(String.valueOf(c), switch (c) {
                    case ',' -> TokenType.Comma;
                    case ';' -> TokenType.SemiColon;
                    case '.' -> TokenType.Dot;
                    default -> TokenType.ExclamationMark;
                }, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + 1));
            } else if (c == '\'' || c == '"') {
                var res = this.resolveIdentifiers();
                if (res != null) return res;
                int n = src.find(c);
                if (n == -1) throw new LangCompileTimeException("No closing string quote found");
                result = new Token(src.slice(n), TokenType.Literal, Location.of(src.crow(), src.ccol(), src.crow(), src.ccol() + n));
                src.jump(n + 1);
            } else src.save();

            src.consume();
        }
        return result;
    }

    private void pushNext() {
        tokens.add(buildNext());
    }

    public Token consume() {
        if (index >= tokens.size()) {
            pushNext();
        }

        return tokens.get(index++);
    }

    public Token lookAhead(int d) {
        while (index + d > tokens.size()) {
            pushNext();
        }
        return tokens.get(index + d - 1);
    }

    public Token lookAhead() {
        return lookAhead(1);
    }

    public Token lookBehind() {
        return tokens.get(index - 1);
    }

    public Token getAt(int i) {
        while (i >= tokens.size()) {
            pushNext();
        }
        return tokens.get(i);
    }

    public Token getLast() {
        return tokens.getLast();
    }

    public boolean empty() {
        return tokens.isEmpty();
    }
}


