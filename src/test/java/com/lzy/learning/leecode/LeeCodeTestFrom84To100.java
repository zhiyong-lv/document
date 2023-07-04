package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class LeeCodeTestFrom84To100 {
    @ParameterizedTest
    @CsvSource({
            "'0,1,0,2,1,0,1,3,2,1,2,1',6",
            "'4,2,0,3,2,5',9",
    })
    void trapTest(String nums, int expected) {
        final int[] height = Arrays.stream(nums.split(",")).mapToInt(Integer::valueOf).toArray();
        Assertions.assertEquals(expected, new Solution42().trap(height));
    }

    @ParameterizedTest
    @CsvSource({
            "'2,1,5,6,2,3',10",
            "'2,4',4",
            "'2,0,2',2",
            "'0,1,2,3,4,5',9",
            "'0,1,2,3,4,5,6,7,8,9',25",
            "'1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1',53",
    })
    void largestRectangleAreaTest(String nums, int expected) {
        final int[] height = Arrays.stream(nums.split(",")).mapToInt(Integer::valueOf).toArray();
        Assertions.assertEquals(expected, new Solution84().largestRectangleArea(height));
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,1",
            "0,1,2",
            "0,2,4",
            "0,3,8",
            "15,0,14",
            "15,1,13",
            "15,2,11",
            "15,3,7",
    })
    void testChangeOneBit(int num, int bitIdx, int expected) {
        Assertions.assertEquals(expected, new Solution89().changeOneBit(num, bitIdx));
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,true",
            "0,2,true",
            "0,4,true",
            "0,3,false",
    })
    void testDiffOnlyOneBit(int num1, int num2, boolean expected) {
        Assertions.assertEquals(expected, new Solution89().diffOnlyOneBit(num1, num2));
    }

    @Test
    void testGrayCode() {
        final List<Integer> rst = new Solution89().grayCode(2);
        System.out.println(rst);
    }

    @Test
    void testSubsetsWithDup() {
        System.out.println(new Solution90().subsetsWithDup(new int[]{1, 2}));
    }

    @Test
    void testMaximalRectangleByIntArr() {
        final Solution85 solution85 = new Solution85();
        final int i1 = solution85.maximalRectangle(new int[]{1});
        Assertions.assertEquals(1, i1);
        final int i2 = solution85.maximalRectangle(new int[]{2, 0, 2, 1, 1});
        Assertions.assertEquals(3, i2);
        final int i3 = solution85.maximalRectangle(new int[]{2, 1, 3, 4, 2});
        Assertions.assertEquals(6, i3);
        final int i4 = solution85.maximalRectangle(new int[]{});
        Assertions.assertEquals(0, i4);

        final char[][] matrix = {{'1', '0', '1', '0', '0'}, {'1', '0', '1', '1', '1'},
                {'1', '1', '1', '1', '1'}, {'1', '0', '0', '1', '0'}};
        final int[][] transform = solution85.transform(matrix);
        Arrays.stream(transform).map(Arrays::toString).forEach(System.out::println);
        Assertions.assertEquals(6, solution85.maximalRectangle(matrix));
    }
}

class Solution85 {
    public int maximalRectangle(char[][] matrix) {
        return Arrays.stream(transform(matrix)).mapToInt(this::maximalRectangle).max().orElse(0);
    }

    int maximalRectangle(int[] matrix) {
        int maximal = 0;
        for (int i = 0; i < matrix.length; i++) {
            int left = i - 1;
            while (left >= 0 && matrix[left] >= matrix[i]) {
                left--;
            }
            int right = i + 1;
            while (right < matrix.length && matrix[right] >= matrix[i]) {
                right++;
            }
            final int newRectangleArea = (right - left - 1) * matrix[i];
            if (newRectangleArea > maximal) {
                maximal = newRectangleArea;
            }
        }
        return maximal;
    }

    int[][] transform(char[][] matrix) {
        int[][] rst = new int[matrix.length][matrix[0].length];
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                rst[row][col] = matrix[row][col] == '0' ? 0 : (0 == row ? 1 : rst[row - 1][col] + 1);
            }
        }
        return rst;
    }
}

class Solution90 {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        final ArrayList<List<Integer>> rst = new ArrayList<>();
        final ArrayList<Integer> last = new ArrayList<>();
        rst.add(last);
        backtrace(nums, 0, rst, last);
        return rst;
    }

    void backtrace(final int[] nums, final int idxFrom, final List<List<Integer>> rst, final List<Integer> last) {
        for (int i = idxFrom; i < nums.length; i++) {
            if (idxFrom != i && nums[i] == nums[i - 1]) {
                continue;
            }
            last.add(nums[i]);
            rst.add(new ArrayList<>(last));
            backtrace(nums, i + 1, rst, last);
            last.remove(last.size() - 1);
        }
    }
}

class Solution87 {

    Map<String, Set<String>> cache = new HashMap<>();

    public static void main(String[] args) {
        final boolean scramble = new Solution87().isScramble("great", "rgeat");
        Assertions.assertTrue(scramble);
    }

    public boolean isScramble(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();

        if (l1 != l2) {
            return false;
        }

        if (l1 == 1) {
            return s1.equals(s2);
        }

        // i - start from s1, j - start from s2, k - (len -1), dp[i][j][k]
        boolean dp[][][] = new boolean[l1][l2][l1];

        // initial when k = 0
        for (int i = 0; i < l1; i++) {
            for (int j = 0; j < l2; j++) {
                dp[i][j][1] = s1.charAt(i) == s2.charAt(j);
            }
        }

        for (int k = 2; k < l1; k++) {
            for (int i = 0; i <= l1 - k; i++) {
                for (int j = 0; j <= l2 - k; j++) {
                    dp[i][j][k] = false;
                    for (int w = 1; w < k; w++) {
                        if ((dp[i][j][w] && dp[i + w][j + w][k - w]) || (dp[i][j + k - w][w] && dp[i + w][j][k - w])) {
                            dp[i][j][k] = true;
                            break;
                        }
                    }
                }
            }
        }

        for (int w = 1; w < l1; w++) {
            if ((dp[0][0][w] && dp[w][w][l1 - w]) || (dp[0][l1 - w][w] && dp[w][0][l1 - w])) {
                return true;
            }
        }

        return false;
    }

    Set<String> scram(String s) {
        if (cache.containsKey(s)) {
            return cache.get(s);
        }

        Set<String> scrams = new HashSet<>();
        int len = s.length();

        if (1 == len) {
            scrams.add(s);
            return scrams;
        }

        for (int i = 1; i < len; i++) {
            final Set<String> scram1 = scram(s.substring(0, i));
            final Set<String> scram2 = scram(s.substring(i, len));
            for (String s1 : scram1) {
                for (String s2 : scram2) {
                    scrams.add(s1 + s2);
                    scrams.add(s2 + s1);
                }
            }
        }

        cache.put(s, scrams);
        return scrams;
    }
}

class Solution89 {
    public List<Integer> grayCode(int n) {
        final Set<Integer> set = new LinkedHashSet();
        int cur = 0;
        set.add(cur);
        backtrace(set, 0, n);
        return new ArrayList<>(set);
    }

    boolean backtrace(Set<Integer> list, int curNum, int n) {
        boolean rst = false;
        if (list.size() < Math.pow(2, n)) {
            for (int i = 0; i < n; i++) {
                int candidate = changeOneBit(curNum, i);
                if (!list.contains(candidate)) {
                    list.add(candidate);
                    if (backtrace(list, candidate, n)) {
                        rst = true;
                        break;
                    }
                    list.remove(list.size() - 1);
                }
            }
        } else {
            rst = diffOnlyOneBit(0, curNum);
        }
        return rst;
    }

    int changeOneBit(int num, int bitIdx) {
        return num ^ (1 << bitIdx);
    }

    boolean diffOnlyOneBit(int num1, int num2) {
        return Integer.bitCount(num1 ^ num2) == 1;
    }
}

class Solution88 {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int idx1 = m - 1;
        int idx2 = n - 1;
        int idx = m + n - 1;

        while (idx2 >= 0) {
            if (nums1[idx1] <= nums2[idx2]) {
                nums1[idx] = nums2[idx2];
                idx2--;
            } else {
                nums1[idx] = nums1[idx1];
                idx1--;
            }
            idx--;
        }
    }
}

class Solution85S {
    public ListNode partition(ListNode head, int x) {
        ListNode newHead = head;
        ListNode cur = head;
        ListNode prev = null;
        ListNode lastLessNode = null, firstLargerOrEqualNode = null;

        while (cur != null) {
            if (cur.val < x) {
                if (Objects.isNull(lastLessNode)) {
                    lastLessNode = cur;
                    newHead = lastLessNode;
                    if (Objects.nonNull(firstLargerOrEqualNode)) {
                        prev.next = cur.next;
                        lastLessNode.next = firstLargerOrEqualNode;
                    }
                } else if (lastLessNode.next == cur) {
                    lastLessNode = cur;
                } else {
                    prev.next = cur.next;
                    lastLessNode.next = cur;
                    cur.next = firstLargerOrEqualNode;
                    lastLessNode = cur;
                }
            } else {
                if (Objects.isNull(firstLargerOrEqualNode)) {
                    firstLargerOrEqualNode = cur;
                }
            }
            prev = cur;
            cur = cur.next;
        }
        return newHead;
    }
}

class Solution84 {
    public int largestRectangleArea(int[] heights) {
        int max = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < heights.length; i++) {
            if (heights[i] > 0) {
                int lastValIdx = i;
                while (lastValIdx + 1 < heights.length && heights[lastValIdx + 1] == heights[lastValIdx]) {
                    lastValIdx++;
                }
                // Try to calculate the max size at the current idx.
                max = Math.max(heights[i] * (lastValIdx - i + 1), max);
                int newLeftLineIdx = i;
                for (int j = stack.size() - 1; j >= 0; j--) {
                    final int leftLineIdx = stack.get(j);
                    final int leftLineHeight = heights[leftLineIdx];
                    if (leftLineHeight > heights[i]) {
                        max = Math.max((lastValIdx - leftLineIdx + 1) * heights[i], max);
                        newLeftLineIdx = leftLineIdx;
                        stack.remove(j);
                    } else {
                        max = Math.max((lastValIdx - leftLineIdx + 1) * leftLineHeight, max);
                        if (leftLineHeight * (heights.length - leftLineIdx + 1) < max) {
                            stack.remove(j);
                        }
                    }
                }
                stack.push(newLeftLineIdx);
                i = lastValIdx;
            } else {
                stack.clear();
            }
        }
        return max;
    }
}

class Solution42 {
    private void trap(int[] height, Stack<Integer> stack, int[] trap) {
        for (int idx = 0; idx < height.length; idx++) {
            int sum = 0;
            if (height[idx] > 0) {
                int lastHeight = 0;
                while (!stack.isEmpty()) {
                    final int pIdx = stack.pop();
                    final int pVal = height[pIdx];
                    sum += (idx - pIdx - 1) * (Math.min(pVal, height[idx]) - lastHeight);
                    if (pVal >= height[idx]) {
                        stack.push(pIdx);
                        break;
                    }
                    lastHeight = pVal;
                }
                stack.push(idx);
            }
            trap[idx] = (idx > 0) ? trap[idx - 1] + sum : sum;
        }
    }

    public int trap(int[] height) {
        int[] dpRst = new int[height.length];
        trap(height, new Stack<>(), dpRst);
        return dpRst[dpRst.length - 1];
    }
}
