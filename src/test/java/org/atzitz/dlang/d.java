package org.atzitz.dlang;

public class d {
    public static void main(String[] args) {
        long m = System.currentTimeMillis();
        int a = 0;
        int b = 0;
        int target = 50_000;
        while (a <= target) {
            while (b <= target) {
                b = b + 1;
            }
            b = 0;
            a = a + 1;
        }

        System.out.println(STR."\{(System.currentTimeMillis() - m) / 1000F}s");
    }
}
