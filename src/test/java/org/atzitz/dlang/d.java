package org.atzitz.dlang;

public class d {
    public static void main(String[] args) {
        long millis = System.currentTimeMillis();
        System.out.println(fib(40));
        System.out.println(STR."\{System.currentTimeMillis() - millis}ms");
    }

    // Fibonacci
    public static int fib(int n) {
        if (n <= 1) {return n;}
        return fib(n - 1) + fib(n - 2);
    }
    
}
