package com.lzy.learning.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class DynamicPlan {
}

class Solution698 {
    public static void main(String[] args) {
        System.out.println(new Solution698().canPartitionKSubsets(new int[]{4, 3, 2, 3, 5, 2, 1}, 4));
    }

    public boolean canPartitionKSubsets(int[] nums, int k) {
        int sum = Arrays.stream(nums).sum();
        if (sum % k != 0) return false;
        int target = sum / k;
        int usedFlag = 0;
        Arrays.sort(nums);
        usedFlag |= 1 << (nums.length - 1);
        return backTrace(nums, usedFlag, target, nums[nums.length - 1], k);
    }

    boolean backTrace(int[] nums, int usedFlag, int target, int sum, int k) {
        if (sum > target) return false;
        else if (sum == target) {
            k--;
            sum = 0;
            if (0 == k) return true;
        }

        for (int i = 0, usedIdx = -1; i < nums.length; i++) {
            if (usedIdx != -1 && nums[usedIdx] == nums[i]) continue;
            if ((usedFlag & (1 << i)) > 0) continue;
            if (nums[i] + sum > target) return false;
            usedFlag |= 1 << i;
            usedIdx = i;
            if (backTrace(nums, usedFlag, target, nums[i] + sum, k)) return true;
            else usedFlag &= ~(1 << i);
        }
        return false;
    }
}

class Solution1046 {
    public static void main(String[] args) {
        System.out.println(new Solution1046().lastStoneWeight(new int[]{2, 7, 4, 1, 8, 1}));
    }

    public int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> bigHeap = new PriorityQueue<Integer>(((o1, o2) -> o2 - o1));
        for (int n : stones) {
            bigHeap.add(n);
        }

        while (bigHeap.size() >= 2) {
            int first = bigHeap.remove();
            int last = bigHeap.remove();
            int rest = first - last;
            if (rest > 0) {
                bigHeap.add(rest);
            }
        }
        return bigHeap.size() > 0 ? bigHeap.remove() : 0;
    }
}

class Solution473 {
    public static void main(String[] args) {
        System.out.println(new Solution473().makesquare(new int[]{100, 100, 100, 100, 100, 100, 100, 100, 4, 100, 2, 2, 100, 100, 100}));
    }

    public boolean makesquare(int[] matchsticks) {
        int sum = Arrays.stream(matchsticks).sum();
        if (sum % 4 != 0) return false;
        int target = sum / 4;
        int[] usedFlag = new int[matchsticks.length];
        Arrays.sort(matchsticks);
        usedFlag[matchsticks.length - 1] = 1;
        return backTrace(matchsticks, usedFlag, target, matchsticks[matchsticks.length - 1], 0);
    }

    boolean backTrace(int[] matchsticks, int[] usedFlag, final int target, int sum, int cnt) {
        if (sum > target) return false;
        else if (sum == target) {
            cnt++;
            sum = 0;
            if (4 == cnt) return true;
        }

        int lastChooseIdx = -1;
        for (int i = 0; i < matchsticks.length; i++) {
            if (usedFlag[i] > 0) continue;
            if (sum + matchsticks[i] > target) break;
            if (lastChooseIdx != -1 && matchsticks[i] == matchsticks[lastChooseIdx]) continue;
            usedFlag[i] = 1;
            lastChooseIdx = i;
            if (backTrace(matchsticks, usedFlag, target, sum + matchsticks[i], cnt)) return true;
            else usedFlag[i] = 0;
        }
        return false;
    }
}

class Solution416 {
    public static void main(String[] args) {
        System.out.println(new Solution416().canPartition(new int[]{3, 3, 3, 4, 5}));
    }

    public boolean canPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 == 1) return false;
        int target = sum / 2;
        int[] dp = new int[target + 1];

        for (int num : nums) {
            for (int i = target; i >= 0; i--) {
                dp[i] = Math.max(dp[i], num > i ? 0 : dp[i - num] + num);
                if (target == dp[i]) return true;
            }
        }

        return false;
    }

    public boolean canPartition2(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 == 1) return false;
        int target = sum / 2;
        int[][] dp = new int[nums.length][target + 1];

        for (int row = 0; row < nums.length; row++) {
            dp[row][0] = 0;
        }

        for (int col = 1; col < dp[0].length; col++) {
            dp[0][col] = (nums[0] > col) ? 0 : nums[0];
        }

        for (int row = 1; row < nums.length; row++) {
            for (int col = 1; col < dp[0].length; col++) {
                dp[row][col] = Math.max(dp[row - 1][col], nums[row] > col ? 0 : dp[row - 1][col - nums[row]] + nums[row]);
                if (target == dp[row][col]) return true;
            }
        }
        return false;
    }
}

class Solution96 {
    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            int sum = 0;
            for (int j = 1; j <= i; j++) {
                sum += dp[j - 1] * dp[i - j];
            }
            dp[i] = sum;
        }
        return dp[n];
    }
}

class Solution343 {
    public static void main(String[] args) {
        System.out.println(new Solution343().integerBreak(10));
    }

    public int integerBreak(int n) {
        int[] dp = new int[n];
        dp[0] = 1;

        for (int i = 1; i < n; i++) {
            int max = 0;
            int num = i + 1;
            for (int j = 1; j < num; j++) {
                int rst = (num - j) * Math.max(dp[j - 1], j);
                if (max < rst) max = rst;
            }
            dp[i] = max;
            System.out.printf("num %d result is %d\n", num, max);
        }

        return dp[n - 1];
    }
}

class Solution63 {
    public static void main(String[] args) {
        int uniquePathsWithObstacles = new Solution63().uniquePathsWithObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}});
        System.out.println(uniquePathsWithObstacles);
    }

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int[][] dp = new int[obstacleGrid.length][obstacleGrid[0].length];

        dp[0][0] = obstacleGrid[0][0] == 1 ? 0 : 1;

        for (int i = 1; i < obstacleGrid.length; i++) {
            dp[i][0] = obstacleGrid[i][0] == 1 ? 0 : dp[i - 1][0];
        }

        for (int i = 1; i < obstacleGrid[0].length; i++) {
            dp[0][i] = obstacleGrid[0][i] == 1 ? 0 : dp[0][i - 1];
        }

        for (int row = 1; row < obstacleGrid.length; row++) {
            for (int col = 1; col < obstacleGrid[0].length; col++) {
                dp[row][col] = obstacleGrid[row][col] == 1 ? 0 : (dp[row - 1][col] + dp[row][col - 1]);
            }
        }

        return dp[obstacleGrid.length - 1][obstacleGrid[0].length - 1];
    }
}

class Solution62 {
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];

        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }

        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }

        for (int row = 1; row < m; row++) {
            for (int col = 1; col < n; col++) {
                dp[row][col] = dp[row - 1][col] + dp[row][col - 1];
            }
        }

        return dp[m - 1][n - 1];
    }
}

class Solution746 {
    public int minCostClimbingStairs(int[] cost) {
        // the least cost when arriving n
        int[] dp = new int[Math.max(2, cost.length + 1)];

        // init
        dp[0] = 0;
        dp[1] = 0;
        for (int i = 2; i <= cost.length; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);
        }
        return dp[cost.length];
    }
}

class Solution509 {
    public int fib(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 0;
        if (n >= 1) dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }
}

class Solution70 {
    public int climbStairs(int n) {
        int[] dp = new int[Math.max(2, n)];
        dp[0] = 1;
        dp[1] = 2;
        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n - 1];
    }
}
