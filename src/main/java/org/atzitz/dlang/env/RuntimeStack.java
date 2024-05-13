package org.atzitz.dlang.env;

public class RuntimeStack {
    /**
     * A wrapper to RuntimeMem to access the stack portion of the memory
     */

    private final RuntimeMem memory;
    public int maxSP = 0;

    public RuntimeStack(RuntimeMem memory) {
        this.memory = memory;
    }

    public void push(int item) {
        memory.set(memory.$SP++, item);
        // maxSP = Math.max(maxSP, memory.$SP);
    }

    public int pop() {
        return memory.get(--memory.$SP);
    }

}
