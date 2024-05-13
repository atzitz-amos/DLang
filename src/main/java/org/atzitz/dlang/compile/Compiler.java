package org.atzitz.dlang.compile;

import lombok.Getter;
import org.atzitz.dlang.compile.bytecode.ByteCode;
import org.atzitz.dlang.compile.parser.Parser;

@Getter
public class Compiler {
    private final String raw;
    private final Parser parser;

    public Compiler(String code) {
        this.raw = code;

        this.parser = new Parser(raw);
    }

    public CompiledObj compile() {
        parser.parse();
        return new CompiledObj(ByteCode.of(parser, raw));
    }
}
