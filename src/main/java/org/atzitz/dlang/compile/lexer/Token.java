package org.atzitz.dlang.compile.lexer;

import org.atzitz.dlang.compile.Location;

public record Token(String value, TokenType type, Location loc) {
}

