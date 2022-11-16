package com.lzy.learning.leecode;

import java.util.HashMap;
import java.util.Map;

public class MyPowNo50 {

}

class Solution50 {
    Map<Integer, Double> cache = new HashMap<>();
    public double myPow(double x, int n) {
        if (cache.containsKey(n)) return cache.get(n);
        final int nextN = n / 2;
        if (n > 0) {
            final double v = (n == 1) ? x : myPow(x, nextN) * myPow(x, nextN) * (n % 2 == 0 ? 1 : x);
            cache.put(n, v);
            return v;
        } else if (n < 0) {
            final double v = (n == -1) ? 1 / x : myPow(x, nextN) * myPow(x, nextN) * (n % 2 == 0 ? 1 : 1 / x);
            cache.put(n, v);
            return v;
        } else {
            return 1;
        }
    }
}
