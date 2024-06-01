package org.atzitz.dlang.exec;

import lombok.Getter;
import org.atzitz.dlang.compile.CompiledObj;
import org.atzitz.dlang.compile.bytecode.bytecodes.*;
import org.atzitz.dlang.env.RuntimeEnv;

@Getter
public class Exec {
    final CompiledObj compiledObj;
    final RuntimeEnv env;

    final int GLOBAL_HEAP_ADDR;

    public Exec(CompiledObj compiledObj) {
        this.compiledObj = compiledObj;
        this.env = new RuntimeEnv(compiledObj);

        GLOBAL_HEAP_ADDR = env.heap.allocAndPosition(compiledObj.getGlobalsC());
    }

    public ExecCompletionInfo exec() {
        long start = System.currentTimeMillis();

        while (env.pc.value < compiledObj.size()) {
            step();
            env.pc.value++;
        }

        return new ExecCompletionInfo(this, System.currentTimeMillis() - start);
    }

    void step() {
        AbstractBytecode bc = compiledObj.at(env.pc.value);

        switch (bc.type) {
            case AbstractBytecode.Type.BinOp -> execBinOp((BCBinOp) bc);
            case AbstractBytecode.Type.Compare -> execCompareOp((BCCompare) bc);

            case AbstractBytecode.Type.Jump -> execJump((BCJump) bc);
            case AbstractBytecode.Type.JumpIfFalse -> execJumpIfFalse((BCJumpIfFalse) bc);

            case AbstractBytecode.Type.LoadConst -> execLoadConst((BCLoadConst) bc);
            case AbstractBytecode.Type.LoadDynamic -> execLoadDynamic((BCLoadDynamic) bc);
            case AbstractBytecode.Type.LoadGlobal -> execLoadGlobal((BCLoadGlobal) bc);
            case AbstractBytecode.Type.LoadRel -> execLoadRel((BCLoadRel) bc);
            case AbstractBytecode.Type.LoadParam -> execLoadParam((BCLoadParam) bc);
            case AbstractBytecode.Type.LoadThis -> env.stack.push(env.heap.$THIS);

            case AbstractBytecode.Type.StoreDynamic -> execStoreDynamic((BCStoreDynamic) bc);
            case AbstractBytecode.Type.StoreGlobal -> execStoreGlobal((BCStoreGlobal) bc);
            case AbstractBytecode.Type.StoreRel -> execStoreRel((BCStoreRel) bc);
            case AbstractBytecode.Type.ExchangeAssign -> execExchangeAssign((BCExchangeAssign) bc);

            case AbstractBytecode.Type.FuncAlloc -> execFuncAlloc((BCFuncAlloc) bc);
            case AbstractBytecode.Type.FuncInit -> execFuncInit((BCInitFunc) bc);

            case AbstractBytecode.Type.BuildCls -> execBuildCls((BCBuildCls) bc);
            case AbstractBytecode.Type.InvokeFunc -> execInvokeFunc((BCInvokeFunc) bc);
            case AbstractBytecode.Type.Return -> execReturn((BCReturn) bc);
        }
    }

    private void saveState(int argc) {
        env.stack.push(env.mem.$LOCAL);
        env.stack.push(env.mem.$PARAM);
        env.stack.push(env.heap.$THIS);
        env.stack.push(env.pc.value);

        env.mem.$PARAM = env.mem.$SP - 4 - argc;
        env.mem.$LOCAL = env.mem.$SP;
    }

    private void execReturn(BCReturn bc) {
        final int retValue = env.stack.pop();
        final int param = env.mem.$PARAM;
        env.mem.$SP = env.mem.$LOCAL;

        env.pc.value = env.stack.pop();
        env.heap.$THIS = env.stack.pop();
        env.mem.$PARAM = env.stack.pop();
        env.mem.$LOCAL = env.stack.pop();

        env.mem.$SP = param;

        env.stack.push(retValue);
    }

    private void execInvokeFunc(BCInvokeFunc bc) {
        if (bc.obj != -1) {
            env.heap.$THIS = env.mem.getLocal(bc.obj);
        }
        int pos = env.heap.getRel(bc.id);

        saveState(bc.argc);
        env.pc.value = pos;
    }

    private void execBuildCls(BCBuildCls bc) {
        int pos = env.mem.getLocal(bc.id);
        saveState(bc.argc);

        env.heap.allocAndPosition(bc.localc);
        env.pc.value = pos;
    }

    private void execFuncAlloc(BCFuncAlloc bc) {
        env.heap.setRel(bc.pointer, bc.offset);
        env.pc.value = bc.until - 1;
    }

    private void execFuncInit(BCInitFunc bc) {
        env.mem.$SP += bc.localc;
    }

    private void execBinOp(BCBinOp bc) {
        final int TOS = env.stack.pop();
        final int TOS1 = env.stack.pop();
        switch (bc.ops) {
            case "+" -> env.stack.push(TOS1 + TOS);
            case "-" -> env.stack.push(TOS1 - TOS);
            case "*" -> env.stack.push(TOS1 * TOS);
            case "/" -> env.stack.push(TOS1 / TOS);
            case "%" -> env.stack.push(TOS1 % TOS);
        }
    }

    private void execCompareOp(BCCompare bc) {
        final int TOS = env.stack.pop();
        final int TOS1 = env.stack.pop();
        switch (bc.ops) {
            case "==" -> env.stack.push(TOS1 == TOS ? 1 : 0);
            case ">" -> env.stack.push(TOS1 > TOS ? 1 : 0);
            case "<" -> env.stack.push(TOS1 < TOS ? 1 : 0);
            case ">=" -> env.stack.push(TOS1 >= TOS ? 1 : 0);
            case "<=" -> env.stack.push(TOS1 <= TOS ? 1 : 0);
        }
    }

    private void execJump(BCJump bc) {
        env.pc.value = bc.jump - 1;
    }

    private void execJumpIfFalse(BCJumpIfFalse bc) {
        if (env.stack.pop() == 0) {
            env.pc.value = bc.jump - 1;
        }
    }

    private void execLoadConst(BCLoadConst bc) {
        env.stack.push(bc.name);
    }

    private void execLoadDynamic(BCLoadDynamic bc) {
        env.stack.push(env.mem.getLocal(bc.id));
    }

    private void execLoadGlobal(BCLoadGlobal bc) {
        env.stack.push(env.mem.get(bc.id));
    }

    private void execLoadParam(BCLoadParam bc) {
        env.stack.push(env.mem.getArg(bc.id));
    }

    private void execLoadRel(BCLoadRel bc) {
        if (bc.obj == -1) {
            env.stack.push(env.heap.getRel(bc.id));
        } else env.stack.push(env.heap.getAbsolute(bc.obj + bc.id));
    }

    private void execStoreDynamic(BCStoreDynamic bc) {
        env.mem.setLocal(bc.id, env.stack.pop());
    }

    private void execStoreGlobal(BCStoreGlobal bc) {
        env.mem.set(bc.id, env.stack.pop());
    }

    private void execStoreRel(BCStoreRel bc) {
        if (bc.obj == -1) {
            env.heap.setRel(bc.id, env.stack.pop());
        } else env.heap.setAbsolute(bc.obj + bc.id, env.stack.pop());
    }

    private void execExchangeAssign(BCExchangeAssign bc) {
        // TODO
        for (int i = 0; i < bc.size; i++) {
            env.mem.set(bc.ids[bc.size - i - 1], env.stack.pop());
        }
    }

}

