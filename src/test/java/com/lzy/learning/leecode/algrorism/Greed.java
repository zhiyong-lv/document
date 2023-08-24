package com.lzy.learning.leecode.algrorism;

import java.util.Arrays;
import java.util.Objects;

public class Greed {
}

class Solution406 {
    public static void main(String[] args) {
        final Solution406 solution = new Solution406();
        final int[][] rst = solution.reconstructQueue(new int[][]{
                {7, 0}, {4, 4}, {7, 1}, {5, 0}, {6, 1}, {5, 2}
        });
        System.out.println(rst);
    }

    public int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people, (p1, p2) -> p1[0] != p2[0] ? p1[0] - p2[0] : p1[1] - p2[1]);
        int[][] result = new int[people.length][2];
        Arrays.stream(result).forEach(r -> r[0] = -1);
        for (int i = 0; i < people.length; i++) {
            for (int j = 0, count = 0, num = people[i][0], rank = people[i][1]; j < result.length; j++) {
                if (result[j][0] < 0) {
                    if (count++ == rank) {
                        result[j] = people[i];
                        break;
                    }
                } else if (result[j][0] == num) {
                    count++;
                }
            }
        }
        return result;
    }

    public int[][] reconstructQueue2(int[][] people) {
        Arrays.sort(people, (p1, p2) -> p1[0] != p2[0] ? p1[0] - p2[0] : p1[1] - p2[1]);
        int[][] result = new int[people.length][2];
        int[] used = new int[people.length];
        for (int[] person : people) {
            final int hight = person[0];
            int n = 0;
            for (int idx = person[1]; idx > 0 && n < used.length; n++) {
                if ((used[n] == 1 && result[n][0] == hight) || used[n] == 0) {
                    idx--;
                }
            }
            while (used[n] == 1) n++;
            used[n] = 1;
            result[n] = person;
        }
        return result;
    }
}

class Solution {
    public boolean lemonadeChange(int[] bills) {
        int fiveRemain = 5;
        int tenRemain = 0;

        if (bills[0] != 5) return false;

        for (int i = 1; i < bills.length; i++) {
            if (bills[i] == 5) {
                fiveRemain += 5;
            } else if (bills[i] == 10) {
                fiveRemain -= 5;
                tenRemain += 10;
            } else if (bills[i] == 20) {
                if (tenRemain > 0) {
                    tenRemain -= 10;
                    fiveRemain -= 5;
                } else {
                    fiveRemain -= 15;
                }
            }
            if (fiveRemain < 0) return false;
        }
        return true;
    }
}

class Solution135 {
    public int candy(int[] ratings) {
        int[] candies = new int[ratings.length];
        candies[0] = 1;
        for (int i = 1; i < ratings.length; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1;
            } else {
                candies[i] = 1;
            }
        }

        for (int i = ratings.length - 1; i > 0; i--) {
            if (ratings[i] < ratings[i - 1]) {
                candies[i - 1] = Math.max(candies[i] + 1, candies[i - 1]);
            }
        }
        return Arrays.stream(candies).sum();
    }
}

class Solution134 {
    public static void main(String[] args) {
        final Solution134 solution = new Solution134();
        final int idx = solution.canCompleteCircuit(new int[]{1, 4, 1, 3}, new int[]{2, 1, 5, 1});
        System.out.println(idx);
    }

    public int canCompleteCircuit(int[] gas, int[] cost) {
        int[] remain = new int[cost.length];
        int sum = 0;
        for (int i = 0; i < cost.length; i++) {
            remain[i] = gas[i] - cost[i];
            sum += remain[i];
        }

        if (sum < 0) return -1;
        int min = Integer.MAX_VALUE, minIdx = -1;
        for (int i = 0, s = 0; i < remain.length; i++) {
            s += remain[i];
            if (s < 0 && s < min) {
                min = s;
                minIdx = i;
            }
        }
        return minIdx + 1;
    }
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
