package org.atzitz.dlang.compile.lexer;

public enum TokenType {
    Identifier,
    Keyword,
    Number,
    String,

    Equals,
    Comparison,
    Operator,
    OpEquals,

    Comma,
    SemiColon,

    OpenParen,
    CloseParen,
    OpenBrace,
    CloseBrace,
    OpenBracket,
    CloseBracket,

    Dot, EOF
}
