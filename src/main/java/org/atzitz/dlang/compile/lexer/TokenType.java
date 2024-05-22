package org.atzitz.dlang.compile.lexer;

public enum TokenType {
    Identifier,
    Keyword,
    Number,
    Literal,

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

    Dot, ExclamationMark, EOF
}
