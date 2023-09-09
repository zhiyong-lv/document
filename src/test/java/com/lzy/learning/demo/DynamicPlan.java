package com.lzy.learning.demo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicPlan {
}

class Solution392 {
    public boolean isSubsequence(String s, String t) {
        int nextIndex = 0;

        for (int i = 0; i < t.length(); i++) {
            if (t.charAt(i) == s.charAt(nextIndex)) nextIndex++;
            if (nextIndex == s.length()) return true;
        }
        return false;
    }

    public boolean isSubsequencePre(String s, String t) {
        int[] dp = new int[s.length() + 1];
        Arrays.fill(dp, -1);

        for (int i = 0; i < s.length(); i++) {
            for (int j = dp[i] + 1; j < t.length(); j++) {
                if (s.charAt(i) == t.charAt(j)) {
                    dp[i + 1] = j;
                    break;
                }
            }
            if (dp[i + 1] == -1) return false;
        }
        return true;
    }

    public boolean isSubsequenceOld(String s, String t) {
        int lastLeastFoundIdx = -1;
        for (int i = 0; i < s.length(); i++) {
            boolean foundThisRound = false;
            for (int j = Math.max(i, lastLeastFoundIdx + 1); j < t.length(); j++) {
                if (s.charAt(i) == t.charAt(j)) {
                    lastLeastFoundIdx = j;
                    foundThisRound = true;
                    break;
                }
            }
            if (!foundThisRound) return false;
        }
        return true;
    }
}

class Solution53 {
    public int maxSubArray(int[] nums) {
        int max = Integer.MIN_VALUE;
        int[] dp = new int[nums.length];
        dp[0] = nums[0];

        for (int i = 1; i < dp.length; i++) {
            dp[i] = Math.max(nums[i] + dp[i - 1], nums[i]);
            max = Math.max(dp[i], max);
        }

        return max;
    }
}

class Solution1035 {
    public int maxUncrossedLines(int[] nums1, int[] nums2) {
        int[][] dp = new int[nums1.length + 1][nums2.length + 1];
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[nums1.length][nums2.length];
    }
}

class Solution1143 {
    public static void main(String[] args) {
        System.out.println(new Solution1143().longestCommonSubsequence("mhunuzqrkzsnidwbun", "szulspmhwpazoxijwbq"));
    }

    public int longestCommonSubsequence(String text1, String text2) {
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[i].length; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[text1.length()][text2.length()];
    }

    public int longestCommonSubsequence2(String text1, String text2) {
        if (text1.length() > text2.length()) {
            String s = text2;
            text2 = text1;
            text1 = s;
        }
        Map<Character, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < text1.length(); i++) {
            char c = text1.charAt(i);
            if (!map.containsKey(c)) map.put(c, new ArrayList<Integer>());
            map.get(c).add(i);
        }
        int[] dp = new int[text1.length()];
        for (int i = 0; i < text2.length(); i++) {
            final char c = text2.charAt(i);
            if (!map.containsKey(c)) continue;
            final List<Integer> indexes = map.get(c);
            for (int k = indexes.size() - 1; k >= 0; k--) {
                int idx = indexes.get(k);
                int preMax = 0;
                for (int j = idx - 1; j >= 0; j--) {
                    preMax = Math.max(preMax, dp[j]);
                }
                dp[idx] = preMax + 1;
            }
        }
        return Arrays.stream(dp).max().getAsInt();
    }

    private int findNextIdx(List<Integer> list, int num) {
        if (num < 0) return 0;
        int start = 0, end = list.size();
        while (start < end) {
            int mid = (start + end) / 2;
            if (num < list.get(mid)) end = mid;
            else start = mid + 1;
        }
        return start;
    }
}

class Solution718 {
    public int findLength(int[] nums1, int[] nums2) {
        int max = 0;
        int[][] dp = new int[nums1.length][nums2.length];

        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                if (nums1[i] == nums2[j]) {
                    dp[i][j] = (i >= 1 && j >= 1 ? dp[i - 1][j - 1] : 0) + 1;
                } else {
                    dp[i][j] = 0;
                }
                max = Math.max(max, dp[i][j]);
            }
        }
        return max;
    }
}

class Solution674 {
    public static void main(String[] args) {
        System.out.println(new Solution674().findLengthOfLCIS(new int[]{1, 3, 5, 4, 7}));
    }

    public int findLengthOfLCIS(int[] nums) {
        int lastVal = 1;
        int result = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > nums[i - 1]) {
                lastVal++;
                result = Math.max(result, lastVal);
            } else {
                lastVal = 1;
            }
        }
        return result;
    }
}

class Solution300 {
    public static void main(String[] args) {
        System.out.println(new Solution300().lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));
    }

    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Integer.MAX_VALUE;
        }
        int max = 1;
        for (int i = 1; i < nums.length; i++) {
            int idx = Arrays.binarySearch(dp, 0, max, nums[i]);
            if (idx >= 0) continue;
            idx = -idx - 1;
            dp[idx] = Math.min(nums[i], dp[idx]);
            max += idx < max ? 0 : 1;
        }
        return max;
    }

    public int lengthOfLISOld(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int result = 0;
        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            result = Math.max(result, dp[i]);
        }
        return result;
    }
}

class Solution714 {
    public int maxProfit(int[] prices, int fee) {
        int[][] dp = new int[prices.length][2];
        dp[0][0] = -prices[0];

        for (int i = 1; i < prices.length; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] - prices[i]);
            dp[i][1] = Math.max(dp[i - 1][0] + prices[i] - fee, dp[i - 1][1]);
        }
        return dp[prices.length - 1][1];
    }
}

class Solution188 {
    public int maxProfit(int k, int[] prices) {
        int[][][] dp = new int[prices.length][k][2];

        for (int i = 0; i < k; i++) {
            dp[0][i][0] = -prices[0];
        }

        for (int i = 1; i < prices.length; i++) {
            dp[i][0][0] = Math.max(dp[i - 1][0][0], -prices[i]);
            dp[i][0][1] = Math.max(dp[i - 1][0][0] + prices[i], dp[i - 1][0][1]);
            for (int j = 1; j < k; j++) {
                dp[i][j][0] = Math.max(dp[i - 1][j][0], dp[i - 1][j - 1][1] - prices[i]);
                dp[i][j][1] = Math.max(dp[i - 1][j][0] + prices[i], dp[i - 1][j][1]);
            }
        }

        return dp[prices.length - 1][k - 1][1];
    }
}

class Solution309 {
    public int maxProfit(int[] prices) {
        int[][] dp = new int[prices.length][2];
        dp[0][0] = -prices[0];

        for (int i = 1; i < prices.length; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], (i >= 2 ? dp[i - 2][1] : 0) - prices[i]);
            dp[i][1] = Math.max(dp[i - 1][1], prices[i] + dp[i - 1][0]);
        }

        return dp[prices.length - 1][1];
    }
}

class Solution123 {
    public static void main(String[] args) {
        System.out.println(new Solution123().maxProfit(new int[]{3, 2, 6, 5, 0, 3}));
    }

    public int maxProfit(int[] prices) {
        int[][] dp = new int[prices.length][4];

        dp[0][0] = -prices[0];
        dp[0][1] = 0;
        dp[0][2] = -prices[0];
        dp[0][3] = 0;

        for (int i = 1; i < prices.length; i++) {
            dp[i][0] = Math.max(-prices[i], dp[i - 1][0]);
            dp[i][1] = Math.max(prices[i] + dp[i - 1][0], dp[i - 1][1]);
            dp[i][2] = Math.max(dp[i - 1][1] - prices[i], dp[i - 1][2]);
            dp[i][3] = Math.max(prices[i] + dp[i - 1][2], dp[i - 1][3]);
        }
        return dp[prices.length - 1][3];
    }
}

class Solution122 {
    public int maxProfit(int[] prices) {
        int[][] dp = new int[prices.length][2];
        dp[0][0] = -prices[0];
        dp[0][1] = 0;
        for (int i = 1; i < dp.length; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] - prices[i]);
            dp[i][1] = Math.max(dp[i - 1][1], prices[i] + dp[i - 1][0]);
        }
        return dp[prices.length - 1][1];
    }
}

class Solution121 {
    public int maxProfit(int[] prices) {
        int[][] dp = new int[prices.length][2];
        dp[0][0] = -prices[0];
        dp[0][1] = 0;
        for (int i = 1; i < dp.length; i++) {
            dp[i][0] = Math.max(-prices[i], dp[i - 1][0]);
            dp[i][1] = Math.max(prices[i] + dp[i - 1][0], dp[i - 1][1]);
        }
        return dp[prices.length - 1][1];
    }

    public int maxProfit2(int[] prices) {
        int[] dp = new int[prices.length];
        dp[0] = 0;
        for (int i = 1; i < dp.length; i++) {
            dp[i] = Math.max(prices[i] - prices[i - 1] + dp[i - 1], 0);
        }
        return Arrays.stream(dp).max().getAsInt();
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution337 {
    public int rob(TreeNode root) {
        final int[] traverse = traverse(root);
        return Math.max(traverse[0], traverse[1]);
    }

    int[] traverse(TreeNode node) {
        if (Objects.isNull(node)) return new int[]{0, 0};
        int[] leftRst = traverse(node.left);
        int[] rightRst = traverse(node.right);
        int[] result = new int[2];
        result[0] = leftRst[1] + rightRst[1] + node.val;
        result[1] = Math.max(leftRst[0], leftRst[1]) + Math.max(rightRst[0], rightRst[1]);
        return result;
    }
}

class Solution213 {
    public static void main(String[] args) {
        System.out.println(new Solution213().rob(new int[]{2, 3, 2}));
    }

    public int rob(int[] nums) {
        int[] dp1 = new int[nums.length];
        int[] dp2 = new int[nums.length];
        dp1[0] = 0;
        dp2[0] = 0;

        for (int i = 1; i < nums.length; i++) {
            dp1[i] = Math.max((i >= 2 ? dp1[i - 2] : 0) + nums[i - 1], dp1[i - 1]);
        }

        for (int i = 1; i < nums.length; i++) {
            dp2[i] = Math.max((i >= 2 ? dp2[i - 2] : 0) + nums[i], dp2[i - 1]);
        }
        return Math.max(dp1[dp1.length - 1], dp2[dp2.length - 1]);
    }
}

class Solution198 {
    public int rob(int[] nums) {
        int[] dp = new int[nums.length];
        if (nums.length > 0) dp[0] = nums[0];
        if (nums.length > 1) dp[1] = nums[1];
        for (int i = 2; i < dp.length; i++) {
            dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
        }
        return dp[dp.length - 1];
    }
}

class Solution139 {
    public static void main(String[] args) {
        System.out.println(new Solution139().wordBreak("acaaaaabbbdbcccdcdaadcdccacbcccabbbbcdaaaaaadb",
                new ArrayList<>(Arrays.asList("abbcbda", "cbdaaa", "b", "dadaaad", "dccbbbc", "dccadd", "ccbdbc", "bbca", "bacbcdd", "a", "bacb", "cbc", "adc", "c", "cbdbcad", "cdbab", "db", "abbcdbd", "bcb", "bbdab", "aa", "bcadb", "bacbcb", "ca", "dbdabdb", "ccd", "acbb", "bdc", "acbccd", "d", "cccdcda", "dcbd", "cbccacd", "ac", "cca", "aaddc", "dccac", "ccdc", "bbbbcda", "ba", "adbcadb", "dca", "abd", "bdbb", "ddadbad", "badb", "ab", "aaaaa", "acba", "abbb"))));
    }

    public boolean wordBreak(String s, List<String> wordDict) {
        wordDict.removeIf(word -> !s.contains(word));
        Map<Integer, List<String>> wordLenDictMap = new HashMap<>();
        for (String word : wordDict) {
            if (!s.contains(word)) continue;
            final int length = word.length();
            if (!wordLenDictMap.containsKey(length)) {
                wordLenDictMap.put(length, new ArrayList<>());
            }
            wordLenDictMap.get(length).add(word);
        }
        final ArrayList<Integer> lengthList = new ArrayList<>(wordLenDictMap.keySet());
        lengthList.sort(Integer::compareTo);

        int[] dp = new int[s.length() + 1];
        dp[0] = 1;
        for (int i = 0; i <= s.length(); i++) {
            for (int length : lengthList) {
                if (i < length || dp[i - length] != 1) continue;
                String subStr = s.substring(s.length() - i, s.length() - i + length);
                if (wordLenDictMap.get(length).contains(subStr)) dp[i] = 1;
            }
        }

        return dp[s.length()] == 1;
    }
}

class Solution279 {
    public static void main(String[] args) {
        System.out.println(new Solution279().numSquares(12));
    }

    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 0;
        for (int j = 1; j <= n; j++) {
            dp[j] = Integer.MAX_VALUE;
        }
        for (int i = 1; i * i <= n; i++) {
            for (int j = i * i; j <= n; j++) {
                dp[j] = Math.min(dp[j], dp[j - i * i] + 1);
            }
            Arrays.stream(dp).forEach(num -> System.out.printf("%d,", num));
            System.out.println("");
        }
        return dp[n];
    }
}

class Solution70Dp {
    public int climbStairs(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= 2; j++) {
                if (i >= j) dp[i] += dp[i - j];
            }
        }
        return dp[n];
    }
}

class Solution377 {
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1;

        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (i >= num) dp[i] += dp[i - num];
            }
        }

        return dp[target];
    }
}

class Solution518 {
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }

        return dp[amount];
    }
}

class Solution322 {
    public static void main(String[] args) {
        System.out.println(new Solution322().coinChange(new int[]{1, 2, 5}, 11));
    }

    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];

        for (int i = 1; i <= amount; i++) {
            dp[i] = Integer.MAX_VALUE;
        }
        Arrays.sort(coins);
        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                if (dp[i - coin] != Integer.MAX_VALUE) dp[i] = Math.min(dp[i - coin] + 1, dp[i]);
            }
        }
        return dp[amount] == Integer.MAX_VALUE ? -1 : dp[amount];
    }
}

class Solution474 {
    public static void main(String[] args) {
        System.out.println(new Solution474().findMaxForm(new String[]{"10", "0001", "111001", "1", "0"}, 5, 3));
    }

    public int findMaxForm(String[] strs, int m, int n) {
        int[][] matrix = new int[strs.length][2];
        for (int i = 0; i < strs.length; i++) {
            matrix[i][0] = strs[i].replaceAll("1", "").length();
            matrix[i][1] = strs[i].replaceAll("0", "").length();
        }

        int[][] dp = new int[m + 1][n + 1];

        for (int k = 0; k < matrix.length; k++) {
            for (int i = m; i >= matrix[k][0]; i--) {
                for (int j = n; j >= matrix[k][1]; j--) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - matrix[k][0]][j - matrix[k][1]] + 1);
                }
            }
        }

        return dp[m][n];
    }
}

class Solution494 {
    public static void main(String[] args) {
        final int targetSumWays = new Solution494().findTargetSumWays(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1);
        System.out.println(targetSumWays);
    }

    public int findTargetSumWays(int[] nums, int target) {
        final int sum = Arrays.stream(nums).sum();
        if ((sum - target) % 2 != 0 || target > sum) return 0;
        final int smallPartSum = (sum - target) / 2;
        int[] dp = new int[smallPartSum + 1];
        for (int i = 0; i < dp.length; i++) {
            dp[i] = i == 0 ? 1 : 0;
        }

        for (int i = 0; i < nums.length; i++) {
            for (int j = smallPartSum; j >= nums[i]; j--) {
                if (j >= nums[i]) {
                    dp[j] += dp[j - nums[i]];
                }
            }
        }
        return dp[smallPartSum];
    }

    public int findTargetSumWaysDfs(int[] nums, int target) {
        final int sum = Arrays.stream(nums).sum();
        if ((sum - target) % 2 != 0 || target > sum) return 0;
        final int groupSum = (sum - target) / 2;
        Arrays.sort(nums);
        AtomicInteger count = new AtomicInteger(0);
        backTracking(nums, groupSum, 0, count, 0);
        return count.get();
    }

    void backTracking(int[] nums, int target, int sum, AtomicInteger count, int start) {
        if (sum == target) count.getAndIncrement();

        for (int i = start; i < nums.length && nums[i] + sum <= target; i++) {
            sum += nums[i];
            backTracking(nums, target, sum, count, i + 1);
            sum -= nums[i];
        }
    }
}

class Solution1049 {
    public int lastStoneWeightII(int[] stones) {
        final int sum = Arrays.stream(stones).sum();
        final int target = sum / 2;
        int[] dp = new int[target + 1];

        for (int j = 0; j <= target; j++) {
            dp[j] = j >= stones[0] ? stones[0] : 0;
        }

        for (int i = 1; i < stones.length; i++) {
            for (int j = target; j >= 0; j--) {
                dp[j] = Math.max(dp[j], j >= stones[i] ? dp[j - stones[i]] + stones[i] : 0);
            }
        }
        return sum - 2 * dp[target];
    }
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
