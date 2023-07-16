package com.lzy.learning.leecode.algrorism;

import java.util.Arrays;
import java.util.Objects;

public class Greed {
}

class Solution376 {
    public static void main(String[] args) {
        int rst = new Solution376().wiggleMaxLength(new int[]{1, 17, 5, 10, 13, 13, 15, 10, 5, 16, 8});
        System.out.println("\n" + rst);
    }

    public int wiggleMaxLength(int[] nums) {
        if (nums.length <= 1) {
            return nums.length;
        }
        int length = 1;
        Boolean largerThanZero = null;
        for (int slow = 0, fast = 1; fast < nums.length; fast++) {
            while (fast < nums.length && nums[fast] == nums[fast - 1]) fast++;
            if (fast == nums.length) break;
            final boolean newLargerThanZero = nums[fast] - nums[fast - 1] > 0;
            if (Objects.isNull(largerThanZero) || newLargerThanZero != largerThanZero) {
                largerThanZero = newLargerThanZero;
                length++;
                slow = fast;
            }
        }
        return length;
    }
}

class Solution455 {
    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int result = 0;
        for (int i = 0, j = 0; i < g.length && j < s.length; ) {
            if (g[i] <= s[j]) {
                result++;
                i++;
                j++;
            } else {
                j++;
            }
        }
        return result;
    }
}
