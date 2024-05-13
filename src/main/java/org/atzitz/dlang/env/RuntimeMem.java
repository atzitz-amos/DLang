package org.atzitz.dlang.env;

import lombok.Getter;
import org.atzitz.dlang.compile.CompiledObj;
import org.atzitz.dlang.compile.parser.ScopeVisitor;

@Getter
public class RuntimeMem {
    private final int[] memory;

    public int $SP;
    public int $LOCAL = 0;
    public int $THIS = 0;
    public int $PARAM = -1;

    public RuntimeMem(CompiledObj obj) {
        memory = new int[67_108_864];

        ScopeVisitor visitor = obj.getScopes().visitor();
        $SP = visitor.getCurrent().getLocals().size() + visitor.getCurrent().getLocalObjects().size() + 1;
    }

    public void set(int id, int value) {
        memory[id] = value;
    }

    public int get(int id) {
        return memory[id];
    }

    public int getLocal(int id) {
        return memory[$LOCAL + id];
    }

    public int getArg(int id) {
        return memory[$PARAM + id];
    }

    public int getRel(int id) {
        return memory[$THIS + id];
    }

    public void setLocal(int id, int val) {
        memory[$LOCAL + id] = val;
    }

    public void setArg(int id, int val) {
        memory[$PARAM + id] = val;
    }

    public void setRel(int id, int val) {
        memory[$THIS + id] = val;
    }

    public void positionThis(int value) {
        $THIS = value;
    }

    public void positionParam(int value) {
        $PARAM = value;
    }

    public void positionLocal(int value) {
        $LOCAL = value;
    }
}
