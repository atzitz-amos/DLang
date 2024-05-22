package org.atzitz.dlang;

import org.atzitz.dlang.exec.ExecCompletionInfo;
import org.junit.jupiter.api.Test;

public class DLangTest {
    @Test
    void test() {
        ExecCompletionInfo i = DLang.exec("""
                class MyClass {
                    int attr = 5;
                    
                    int getAttr() {
                        return attr;
                    }
                    
                    int add(int a, int b) {
                        return a + b;
                    }
                    
                    int fib(int n) {
                        if (n <= 1) {return n;}
                        return fib(n - 1) + fib(n - 2);
                    }
                }
                                
                MyClass cls = new MyClass();
                                
                int x = cls.fib(20);
                """);

        System.out.println(i.exec().getEnv().mem.get(1));
        System.out.println(STR."\{i.executionTime()}ms");

    }
}