package org.atzitz.dlang.env;

import org.atzitz.dlang.compile.CompiledObj;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class RuntimeHeap {
    public final int size = 67_108_864;

    private final int[] memory;
    private final ArrayList<Pair>[] arr;

    public int $THIS;

    public RuntimeHeap(CompiledObj obj) {
        int x = (int) Math.ceil(Math.log(size) / Math.log(2));

        memory = new int[size];
        arr = new ArrayList[x + 1];

        for (int i = 0; i <= x; i++)
            arr[i] = new ArrayList<>();

        arr[x].add(new Pair(0, size - 1));
    }

    public void setAbsolute(int id, int value) {
        memory[id] = value;
    }

    public int getAbsolute(int id) {
        return memory[id];
    }

    public int alloc(int size) {
        int x = (int) Math.ceil(Math.log(size) / Math.log(2));

        int i;
        Pair temp;

        if (!arr[x].isEmpty()) {
            temp = arr[x].removeFirst();
            return temp.lb;
        }

        for (i = x + 1; i < arr.length; i++) {

            if (arr[i].isEmpty())
                continue;

            break;
        }

        if (i == arr.length) {
            throw new OutOfMemoryError();
        }

        temp = arr[i].removeFirst();

        i--;

        for (; i >= x; i--) {
            Pair p1 = new Pair(temp.lb, temp.lb
                    + (temp.ub - temp.lb) / 2);

            Pair p2 = new Pair(temp.lb
                    + (temp.ub - temp.lb + 1) / 2, temp.ub);

            arr[i].add(p1);
            arr[i].add(p2);

            temp = arr[i].removeFirst();
        }
        return temp.lb;
    }

    public int allocAndPosition(int s) {
        return ($THIS = alloc(s));
    }

    public int getRel(int id) {
        return memory[$THIS + id];
    }

    public void setRel(int id, int val) {
        memory[$THIS + id] = val;
    }

    public void positionThis(int value) {
        $THIS = value;
    }

    private static class Pair {
        int lb, ub;

        Pair(int a, int b) {
            lb = a;
            ub = b;
        }
    }
}