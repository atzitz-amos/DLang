package org.atzitz.dlang;

public class d {
    public static void main(String[] args) {
        long millis = System.currentTimeMillis();
        System.out.println(STR."Using regular fib: \{fib(4_000_000_000L)}");
        System.out.println(STR."\{System.currentTimeMillis() - millis}ms");

        long millis2 = System.currentTimeMillis();
        System.out.println(STR."Using segmented fib: \{segmentedFib(4_000_000_000L)}");
        System.out.println(STR."\{System.currentTimeMillis() - millis2}ms");

    }

    // Fibonacci
    public static long fib(long n) {
        long a = 0, b = 1;
        long i = 0;
        while (i < n) {
            long temp = a;
            a = b;
            b = temp + b;
            i++;
        }
        return b;
    }

    public static long segmentedFib(long n) {
        long a = 0, b = 1;
        long i = 0;
        while (i + 20 < n) {
            long temp = a;
            a = 4181 * a + 6765 * b;
            b = 6765 * temp + 10946 * b;
            i += 20;
        }
        while (i < n) {
            long temp = a;
            a = b;
            b = temp + b;
            i++;
        }
        return b;
    }

}
