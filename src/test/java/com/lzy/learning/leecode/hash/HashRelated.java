package com.lzy.learning.leecode.hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HashRelated {
    public static void main(String[] args) {
        // System.out.printf("result is: %s\n", new Solution18().fourSum(new int[]{1,0,-1,0,-2,2}, 0));
        // System.out.printf("result is: %s\n", new Solution18().fourSum(new int[]{-3,-2,-1,0,0,1,2,3}, 0));
        // System.out.printf("result is: %s\n", new Solution18().fourSum(new int[]{1,0,-1,0,-2,2}, 0));
        System.out.printf("result is: %s\n", new Solution18().fourSum(new int[]{1,-2,-5,-4,-3,3,3,5}, -11));
    }
}

class Solution242 {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        Map<Character, Integer> charCountMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }
        for (char c : t.toCharArray()) {
            final Integer count = charCountMap.getOrDefault(c, -1);
            if (count > 0) {
                charCountMap.put(c, count - 1);
            } else {
                return false;
            }
        }
        return true;
    }
}

class Solution242NewSolution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] charCount = new int[26];
        for (char c : s.toCharArray()) {
            charCount[c - 'a'] += 1;
        }
        for (char c : t.toCharArray()) {
            charCount[c - 'a'] -= 1;
            if (charCount[c - 'a'] < 0) {
                return false;
            }
        }
        return true;
    }
}

class Solution1002 {
    public List<String> commonChars(String[] words) {
        final int[][] charCount = new int[words.length][26];
        for (int i = 0; i < words.length; i++) {
            final String word = words[i];
            for (final char c : word.toCharArray()) {
                charCount[i][c - 'a'] += 1;
            }
        }

        List<String> rst = new ArrayList<>();
        for (int c = 0, minCntAmongAllWords = Integer.MAX_VALUE; c <= 'z' - 'a'; c++, minCntAmongAllWords = Integer.MAX_VALUE) {
            for (int i = 0; i < words.length; i++) {
                if (charCount[i][c] < minCntAmongAllWords) {
                    minCntAmongAllWords = charCount[i][c];
                }
            }
            for (int i = 0; i < minCntAmongAllWords; i++) {
                rst.add(String.valueOf((char) (c + 'a')));
            }
        }
        return rst;
    }
}

class Solution349 {
    public int[] intersection(int[] nums1, int[] nums2) {
        int[] rst = new int[nums1.length];
        Set<Integer> set = new HashSet<>();
        Set<Integer> rstSet = new HashSet<>();

        for (int i : nums1) {
            set.add(i);
        }
        int idx = 0;
        for (int i : nums2) {
            if (set.contains(i) && !rstSet.contains(i)) {
                rstSet.add(i);
                rst[idx++] = i;
            }
        }
        return Arrays.copyOfRange(rst, 0, idx);
    }
}

class Solution349Solution2 {
    public int[] intersection(int[] nums1, int[] nums2) {
        int[] rst = new int[nums1.length];
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        int i1 = 0, i2 = 0, rIdx = 0;
        while (i1 < nums1.length && i2 < nums2.length) {
            if (nums1[i1] == nums2[i2] && ((rIdx > 0 && rst[rIdx - 1] != nums1[i1]) || rIdx == 0)) {
                // move
                rst[rIdx++] = nums1[i1];
                i1++;
                i2++;
            } else if (nums1[i1] < nums2[i2]) {
                i1++;
            } else {
                i2++;
            }
        }
        return Arrays.copyOfRange(rst, 0, rIdx);
    }
}

class Solution349Solution3 {
    public int[] intersection(int[] nums1, int[] nums2) {
        int[] rst = new int[nums1.length];
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        int rIdx = 0;
        for (int num : nums2) {
            final int i = binarySearch(nums1, num);
            if (i >= 0 && (rIdx == 0 || (rst[rIdx - 1] != num))) {
                rst[rIdx++] = num;
            }
        }
        return Arrays.copyOfRange(rst, 0, rIdx);
    }

    int binarySearch(int[] a, int key) {
        int start = 0, end = a.length - 1;
        while (start <= end) {
            final int idx = (end + start) / 2;
            if (a[idx] == key) {
                return idx;
            }
            if (a[idx] < key) {
                start = idx + 1;
            } else {
                end = idx - 1;
            }
        }
        return -1;
    }
}

class Solution202 {
    public boolean isHappy(int n) {
        Set<Integer> cache = new HashSet<>();
        while (!cache.contains(n) && n != 1) {
            cache.add(n);
            n = get(n);
        }
        return n == 1;
    }

    private int get(int n) {
        int sum = 0;
        while (n > 0) {
            int rest = n % 10;
            sum += rest * rest;
            n = n / 10;
        }
        return sum;
    }
}

/**
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那 两个 整数，并返回它们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 * <p>
 * 你可以按任意顺序返回答案。
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[0,1]
 * 解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
 * 示例 2：
 * <p>
 * 输入：nums = [3,2,4], target = 6
 * 输出：[1,2]
 * 示例 3：
 * <p>
 * 输入：nums = [3,3], target = 6
 * 输出：[0,1]
 * 提示：
 * <p>
 * 2 <= nums.length <= 104
 * -109 <= nums[i] <= 109
 * -109 <= target <= 109
 * 只会存在一个有效答案
 */
class Solution1 {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, List<Integer>> cache = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (cache.containsKey(nums[i])) {
                cache.get(i).add(i);
            } else {
                List<Integer> l = new ArrayList<>();
                l.add(i);
                cache.put(nums[i], l);
            }
        }
        for (int i = 0; i < nums.length; i++) {
            final List<Integer> list = cache.get(nums[i]);
            list.remove(0);
            if (list.isEmpty()) {
                cache.remove(nums[i]);
            }

            int need = target - nums[i];
            if (cache.containsKey(need)) {
                return new int[]{i, cache.get(need).get(0)};
            }
        }
        throw new IllegalStateException();
    }
}

class Solution1NewSolution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> cache = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            cache.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            final Integer needIdx = cache.getOrDefault(target - nums[i], -1);
            if (needIdx > 0 && needIdx != i) {
                return new int[]{i, needIdx};
            }
        }
        throw new IllegalStateException();
    }
}

/**
 * 给你四个整数数组 nums1、nums2、nums3 和 nums4 ，数组长度都是 n ，请你计算有多少个元组 (i, j, k, l) 能满足：
 * <p>
 * 0 <= i, j, k, l < n
 * nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0
 * 示例 1：
 * <p>
 * 输入：nums1 = [1,2], nums2 = [-2,-1], nums3 = [-1,2], nums4 = [0,2]
 * 输出：2
 * 解释：
 * 两个元组如下：
 * 1. (0, 0, 0, 1) -> nums1[0] + nums2[0] + nums3[0] + nums4[1] = 1 + (-2) + (-1) + 2 = 0
 * 2. (1, 1, 0, 0) -> nums1[1] + nums2[1] + nums3[0] + nums4[0] = 2 + (-1) + (-1) + 0 = 0
 * 示例 2：
 * <p>
 * 输入：nums1 = [0], nums2 = [0], nums3 = [0], nums4 = [0]
 * 输出：1
 * 提示：
 * <p>
 * n == nums1.length
 * n == nums2.length
 * n == nums3.length
 * n == nums4.length
 * 1 <= n <= 200
 * -228 <= nums1[i], nums2[i], nums3[i], nums4[i] <= 228
 */
class Solution454 {
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        Map<Integer, Integer> group1 = new HashMap<>();

        int fourSumCount = 0;
        for (int n1 : nums1) {
            for (int n2 : nums2) {
                group1.put(n1 + n2, group1.getOrDefault(n1 + n2, 0) + 1);
            }
        }
        for (int n3 : nums3) {
            for (int n4 : nums4) {
                fourSumCount += group1.getOrDefault(0 - (n3 + n4), 0);
            }
        }
        return fourSumCount;
    }
}

/**
 * 给你两个字符串：ransomNote 和 magazine ，判断 ransomNote 能不能由 magazine 里面的字符构成。
 * <p>
 * 如果可以，返回 true ；否则返回 false 。
 * <p>
 * magazine 中的每个字符只能在 ransomNote 中使用一次。
 * <p>
 * 示例 1：
 * <p>
 * 输入：ransomNote = "a", magazine = "b"
 * 输出：false
 * 示例 2：
 * <p>
 * 输入：ransomNote = "aa", magazine = "ab"
 * 输出：false
 * 示例 3：
 * <p>
 * 输入：ransomNote = "aa", magazine = "aab"
 * 输出：true
 * 提示：
 * <p>
 * 1 <= ransomNote.length, magazine.length <= 105
 * ransomNote 和 magazine 由小写英文字母组成
 * Related Topics
 * 哈希表
 * 字符串
 * 计数
 */
class Solution383 {
    public boolean canConstruct(String ransomNote, String magazine) {
        Map<Character, Integer> count = new HashMap<>();
        for (char c : magazine.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }
        for (char c : ransomNote.toCharArray()) {
            if (count.getOrDefault(c, 0) > 0) {
                count.put(c, count.get(c) - 1);
            } else {
                return false;
            }
        }
        return true;
    }
}

class Solution383NewSolution {
    public boolean canConstruct(String ransomNote, String magazine) {
        int[] count = new int[26];
        for (char c : magazine.toCharArray()) {
            count[c - 'a'] += 1;
        }
        for (char c : ransomNote.toCharArray()) {
            if (count[c - 'a'] > 0) {
                count[c - 'a'] -= 1;
            } else {
                return false;
            }
        }
        return true;
    }
}

/**
 * 给你一个整数数组 nums ，判断是否存在三元组 [nums[i], nums[j], nums[k]] 满足 i != j、i != k 且 j != k ，同时还满足 nums[i] + nums[j] + nums[k] == 0 。请
 * <p>
 * 你返回所有和为 0 且不重复的三元组。
 * <p>
 * 注意：答案中不可以包含重复的三元组。
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [-1,0,1,2,-1,-4]
 * 输出：[[-1,-1,2],[-1,0,1]]
 * 解释：
 * nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0 。
 * nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0 。
 * nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0 。
 * 不同的三元组是 [-1,0,1] 和 [-1,-1,2] 。
 * 注意，输出的顺序和三元组的顺序并不重要。
 * 示例 2：
 * <p>
 * 输入：nums = [0,1,1]
 * 输出：[]
 * 解释：唯一可能的三元组和不为 0 。
 * 示例 3：
 * <p>
 * 输入：nums = [0,0,0]
 * 输出：[[0,0,0]]
 * 解释：唯一可能的三元组和为 0 。
 * 提示：
 * <p>
 * 3 <= nums.length <= 3000
 * -105 <= nums[i] <= 105
 */
class Solution15 {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> rst = new ArrayList<>();
        Arrays.sort(nums);
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            countMap.put(nums[i], i);
        }
        for (int firstIdx = 0; firstIdx < nums.length - 2; firstIdx++) {
            for (int secondIdx = firstIdx + 1; secondIdx < nums.length - 1; secondIdx++) {
                int target = -nums[firstIdx] - nums[secondIdx];
                if (target < 0) {
                    break;
                }
                final Integer thirdIdx = countMap.getOrDefault(target, -1);
                if (thirdIdx > secondIdx) {
                    List<Integer> match = Arrays.asList(nums[firstIdx], nums[secondIdx], nums[thirdIdx]);
                    rst.add(match);
                }
                while (secondIdx + 1 < nums.length - 1 && nums[secondIdx + 1] == nums[secondIdx]) {
                    secondIdx++;
                }
            }
            if (firstIdx + 1 < nums.length - 2 && nums[firstIdx + 1] == nums[firstIdx]) {
                while (firstIdx + 1 < nums.length - 2 && nums[firstIdx + 1] == nums[firstIdx]) {
                    firstIdx++;
                }
            }
        }
        return rst;
    }
}

class Solution18 {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> rst = new ArrayList<>();
        for (int num1Idx = 0; num1Idx < nums.length - 3; ) {
            for (int num2Idx = num1Idx + 1; num2Idx < nums.length - 2; ) {
                for (int num3Idx = num2Idx + 1, num4Idx = nums.length - 1; num3Idx < num4Idx; ) {
                    final int sum = nums[num1Idx] + nums[num2Idx] + nums[num3Idx] + nums[num4Idx];
                    if ((long)nums[num1Idx] + (long)nums[num2Idx] + (long)nums[num3Idx] + (long)nums[num4Idx] != sum) {
                        break;
                    }
                    if (sum == target) {
                        rst.add(Arrays.asList(nums[num1Idx], nums[num2Idx], nums[num3Idx], nums[num4Idx]));
                        do { num3Idx++; } while (num3Idx < num4Idx && nums[num3Idx] == nums[num3Idx - 1]);
                        do { num4Idx--; } while (num3Idx < num4Idx && nums[num4Idx] == nums[num4Idx + 1]);
                    } else if (sum > target) {
                        do { num4Idx--; } while (num3Idx < num4Idx && nums[num4Idx] == nums[num4Idx + 1]);
                    } else {
                        do { num3Idx++; } while (num3Idx < num4Idx && nums[num3Idx] == nums[num3Idx - 1]);
                    }
                }
                do { num2Idx++; } while (num2Idx < nums.length - 2 && nums[num2Idx] == nums[num2Idx - 1]);
            }
            do { num1Idx++; } while (num1Idx < nums.length - 2 && nums[num1Idx] == nums[num1Idx - 1]);
        }
        return rst;
    }
}