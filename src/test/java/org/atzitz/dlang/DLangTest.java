package org.atzitz.dlang;

import org.atzitz.dlang.exec.ExecCompletionInfo;
import org.junit.jupiter.api.Test;

public class DLangTest {
    @Test
    void test() {
        ExecCompletionInfo i = DLang.exec("""
                int fib(int n) {
                    if (n <= 1) {
                        return n;
                    }
                    return fib(n - 1) + fib(n - 2);
                }
                int i = 0;
                while (i < 30) {
                    fib(i);
                    i = i + 1;
                }
                """);
        System.out.println(i.exec().getEnv().mem.get(0));
        System.out.println(STR."\{i.executionTime()}ms");

    }
}