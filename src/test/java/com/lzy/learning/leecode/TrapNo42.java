package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class TrapNo42 {
    @Test
    void trapTest() {
        int[] height;
        int rst;
        height = new int[]{4, 2, 0, 3, 2, 5};
        rst = trap(height);
        Assertions.assertEquals(9, rst);
        height = new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        rst = trap(height);
        Assertions.assertEquals(6, rst);
        height = new int[]{5, 5, 1, 7, 1, 1, 5, 2, 7, 6};
        rst = trap(height);
        Assertions.assertEquals(23, rst);
    }

    public int trap(int[] height) {
        int sum = 0;
        List<List<Integer>> barriers = new LinkedList<>();
        for (int i = 1; i < height.length - 1; i++) {
            sum += dpt(height, i, barriers);
        }
        return sum;
    }

    private int dpt(int[] height, int index, List<List<Integer>> barriers) {
        if (!barriers.isEmpty()) {
            List<Integer> barrier = barriers.get(barriers.size() - 1);
            if (index <= barrier.get(1)) {
                return 0;
            }
        }

        int prev = index;
        int next = index;
        int v = 0;
        while (prev - 1 >= 0 && height[prev - 1] >= height[prev]) {
            prev--;
            v += height[prev];
        }
        while (next + 1 < height.length && height[next + 1] >= height[next]) {
            next++;
            v += height[next];
        }

        if (index != prev && index != next) {
            barriers.add(Arrays.asList(prev, next));
            return Math.min(height[prev], height[next]) * (next - prev - 1) - v;
        }

        return 0;
    }

    @Test
    void trapAllHeapTest() {
        int[] height;
        int rst;
        height = new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        trap(height, 0).forEach(arr -> System.out.printf("[%s,%s],", arr[0], arr[1]));
        System.out.println("");
        height = new int[]{4, 2, 0, 3, 2, 5};
        trap(height, 0).forEach(arr -> System.out.printf("[%s,%s]", arr[0], arr[1]));
        System.out.println("");
        height = new int[]{5, 5, 1, 7, 1, 1, 5, 2, 7, 6};
        trap(height, 0).forEach(arr -> System.out.printf("[%s,%s]", arr[0], arr[1]));
        System.out.println("");
    }

    public int trap1(int[] height) {
        List<int[]> heaps = trap(height, 0);
        int sums = 0;

        int start = heaps.get(0)[0];
        for (int i = 1; i < heaps.size(); i++) {
            int end = heaps.get(i)[0];
            if (end <= start) {
                continue;
            }

            if (height[end] >= height[start]) {
                sums += count(height, start, end);
                start = end;
            }
        }

        start = heaps.get(heaps.size() - 1)[0];
        for (int i = heaps.size() - 2; i >= 0; i--) {
            int end = heaps.get(i)[0];
            if (end >= start) {
                continue;
            }

            if (height[end] > height[start]) {
                sums += count(height, end, start);
                start = end;
            }
        }

        return sums;
    }

    private int count(int[] height, int start, int end) {
        int minHeight = Math.min(height[start], height[end]);
        int sum = minHeight * (end - start - 1);
        for (int i = start + 1; i < end; i++) {
            sum -= Math.min(height[i], minHeight);
        }
        return sum;
    }

    private List<int[]> trap(int[] height, int from) {
        List<int[]> indexes = new LinkedList<>();
        Predicate<Integer> outOfBounds = index -> index < 0 || index >= height.length;
        for (int i = from; i >= 0 && i < height.length; ) {
            int iPrev = i;
            do {
                iPrev = iPrev - 1;
            } while (!outOfBounds.test(iPrev) && height[i] == height[iPrev]);
            int iNext = i;
            do {
                iNext = iNext + 1;
            } while (!outOfBounds.test(iNext) && height[i] == height[iNext]);

            if ((outOfBounds.test(iNext) || height[i] > height[iNext]) && (outOfBounds.test(iPrev) || height[i] > height[iPrev])) {
                if (!indexes.isEmpty()) {
                    indexes.get(indexes.size() - 1)[1] = iPrev + 1;
                }
                indexes.add(new int[]{iNext - 1, -1});
            }
            i = iNext;
        }
        return indexes;
    }
}
