package org.atzitz.dlang.exec;

import lombok.RequiredArgsConstructor;
import org.atzitz.dlang.env.RuntimeHeap;
import org.atzitz.dlang.env.RuntimeMem;
import org.atzitz.dlang.env.RuntimeStack;

@RequiredArgsConstructor
public class DebuggerFacilities {
    private final Exec _exec;

    public void step() {
        _exec.step();
    }

    public int getPC() {
        return _exec.env.pc.value;
    }

    public void setPC(int value) {
        _exec.env.pc.value = value;
    }

    public void incPC() {
        _exec.env.pc.value++;
    }

    public RuntimeMem getMemory() {
        return _exec.env.mem;
    }

    public RuntimeHeap getHeap() {
        return _exec.env.heap;
    }

    public RuntimeStack getStack() {
        return _exec.env.stack;
    }

    public int getMaxSP() {
        return _exec.env.stack.maxSP;
    }
}
