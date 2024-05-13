package org.atzitz.dlang.env;

import org.atzitz.dlang.compile.CompiledObj;

public class RuntimeEnv {
    public final RuntimeMem mem;
    public final RuntimeStack stack;

    public final RuntimePC pc = new RuntimePC();


    public RuntimeEnv(CompiledObj obj) {
        mem = new RuntimeMem(obj);
        stack = new RuntimeStack(mem);
    }
}
