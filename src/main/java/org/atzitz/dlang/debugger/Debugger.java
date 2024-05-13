package org.atzitz.dlang.debugger;

import org.atzitz.dlang.DLang;
import org.atzitz.dlang.compile.CompiledObj;
import org.atzitz.dlang.compile.bytecode.bytecodes.AbstractBytecode;
import org.atzitz.dlang.exec.DebuggerFacilities;
import org.atzitz.dlang.exec.Exec;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class Debugger {
    private CompiledObj _compiledObj;
    private DebuggerFacilities _debugger;

    public void load(String query) {
        _compiledObj = DLang.compile(query);
        _debugger = new DebuggerFacilities(new Exec(_compiledObj));
    }

    public AbstractBytecode step() {
        AbstractBytecode bc = currentBytecode();
        _debugger.step();
        _debugger.incPC();
        return bc;
    }

    public AbstractBytecode currentBytecode() {
        return _compiledObj.at(_debugger.getPC());
    }

    public AbstractBytecode bytecodeAt(Integer pc) {
        return _compiledObj.at(pc);
    }

    public int[] getMemoryView() {
        return _debugger.getMemory().getMemory();
    }

    public int[] getMemoryView(int start, int end) {
        return Arrays.copyOfRange(_debugger.getMemory().getMemory(), start, end);
    }

    public int getMemoryAt(int index) {
        return _debugger.getMemory().getMemory()[index];
    }

    public int $SP() {
        return _debugger.getMemory().$SP;
    }

    public int $THIS() {
        return _debugger.getMemory().$THIS;
    }

    public int $ARG() {
        return _debugger.getMemory().$PARAM;
    }

    public int $LOCAL() {
        return _debugger.getMemory().$LOCAL;
    }

    public int getPC() {
        return _debugger.getPC();
    }

    public int getMaxSP() {
        return _debugger.getMaxSP();
    }
}
