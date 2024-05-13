package org.atzitz.dlang;


import lombok.Getter;
import org.atzitz.dlang.compile.CompiledObj;
import org.atzitz.dlang.compile.Compiler;
import org.atzitz.dlang.exec.Exec;
import org.atzitz.dlang.exec.ExecCompletionInfo;

@Getter
public class DLang {

    public static CompiledObj compile(String code) {
        return new Compiler(code).compile();
    }

    public static ExecCompletionInfo exec(String code) {
        return exec(compile(code));
    }

    public static ExecCompletionInfo exec(CompiledObj compiled) {
        return new Exec(compiled).exec();
    }

}
