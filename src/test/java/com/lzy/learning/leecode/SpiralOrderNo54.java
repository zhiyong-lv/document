package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;

public class SpiralOrderNo54 {
    private static Stream<Arguments> provideArraysForExist() {
        return Stream.of(
                Arguments.of(new char[][]{new char[]{'A', 'B', 'C', 'E'}, new char[]{'S', 'F', 'C', 'S'}, new char[]{'A', 'D', 'E', 'E'}}, "ABCCED", true),
                Arguments.of(new char[][]{new char[]{'A', 'B', 'C', 'E'}, new char[]{'S', 'F', 'C', 'S'}, new char[]{'A', 'D', 'E', 'E'}}, "ABCB", false),
                Arguments.of(new char[][]{new char[]{'a'}}, "a", true),
                Arguments.of(new char[][]{new char[]{'a', 'a'}}, "aaa", false)
        );
    }

    private static Stream<Arguments> provideArraysForSortColors() {
        return Stream.of(
                Arguments.of(new int[]{2, 0, 2, 1, 1, 0}, new int[]{0, 0, 1, 1, 2, 2}),
                Arguments.of(new int[]{1, 0}, new int[]{0, 1}),
                Arguments.of(new int[]{2, 0, 1}, new int[]{0, 1, 2})
        );
    }

    private static Stream<Arguments> provideArraysForSearchMatrix() {
        return Stream.of(
                Arguments.of(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 3, true),
                Arguments.of(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 13, false)
        );
    }

    private static Stream<Arguments> provideArraysForSetZeroesTest() {
        return Stream.of(
                Arguments.of(new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}}, new int[][]{{1, 0, 1}, {0, 0, 0}, {1, 0, 1}}),
                Arguments.of(new int[][]{{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}}, new int[][]{{0, 0, 0, 0}, {0, 4, 5, 0}, {0, 3, 1, 0}})
        );
    }

    // @Test
    void spiralOrder() {
        int[][] matrix = new int[][]{new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{7, 8, 9}};
        System.out.println(new Solution54().spiralOrder(matrix));
    }

    // @Test
    void canJumpTest() {
        int[] nums = new int[]{2, 3, 1, 1, 4};
        System.out.println(new Solution55().canJump(nums));
    }

    // @Test
    void mergeTest() {
        // [[1,3],[2,6],[8,10],[15,18]]
        int[][] intervals = new int[][]{new int[]{1, 3}, new int[]{2, 6}, new int[]{8, 10}, new int[]{15, 18}};
        System.out.println(Arrays.toString(new Solution56().merge(intervals)));
    }

    // @Test
    void insertTest() {
        // [[1,3],[2,6],[8,10],[15,18]]
        // int[][] intervals = new int[][]{new int[]{1, 3}, new int[]{6, 9}};
        // int[] newInterval = new int[]{2, 5};
        // [[1,2],[3,5],[6,7],[8,10],[12,16]]
        // [4,8]
        // int[][] intervals = new int[][]{new int[]{1, 2}, new int[]{3, 5}, new int[]{6, 7}, new int[]{8, 10}, new int[]{12, 16}};
        // int[] newInterval = new int[]{4, 8};
        int[][] intervals = new int[][]{new int[]{1, 5}};
        int[] newInterval = new int[]{6, 8};
        final int[][] rst = new Solution57().insert(intervals, newInterval);
        Arrays.stream(rst).map(Arrays::toString).forEach(System.out::println);
    }

    // @ParameterizedTest
    // @CsvSource({
    //         "3,3,213",
    //         "3,2,132",
    // })
    void getPermutation(int n, int k, String expected) {
        Assertions.assertEquals(expected, new Solution60().getPermutation(n, k));
    }

    @Test
    void rotateRightTest() {
        int[] in = new int[]{1};
        ListNode node = null;
        for (int i = in.length - 1; i >= 0; i--) {
            node = new ListNode(i, node);
        }
        new Solution61().rotateRight(node, 1);
    }

    @ParameterizedTest
    @CsvSource({
            "'/a//b////c/d//././/..','/a/b/c'"
    })
    void simplifyPathTest(String path, String expected) {
        Assertions.assertEquals(expected, new Solution71().simplifyPath(path));
    }

    @ParameterizedTest
    @CsvSource({
            "'','',0",
            "'a','a',0",
            "'aa','a',1",
            "'a','ab',1",
            "'horse','ros',3",
            "'intention','execution',5",
    })
    void simplifyPathTest(String word1, String word2, int expected) {
        Assertions.assertEquals(expected, new Solution72().minDistance(word1, word2));
    }

    @ParameterizedTest
    @MethodSource("provideArraysForSetZeroesTest")
    void setZeroesTest(int[][] matrix, int[][] expected) {
        new Solution73().setZeroes(matrix);
        Assertions.assertEquals(Arrays.deepToString(expected), Arrays.deepToString(matrix));
    }

    @ParameterizedTest
    @MethodSource("provideArraysForSearchMatrix")
    void searchMatrixTest(int[][] matrix, int target, boolean expected) {
        Assertions.assertEquals(expected, new Solution74().searchMatrix(matrix, target));
    }

    @ParameterizedTest
    @MethodSource("provideArraysForSortColors")
    void sortColorsTest(int[] nums, int[] expected) {
        new Solution75().sortColors(nums);
        Assertions.assertArrayEquals(expected, nums);
    }

    @ParameterizedTest
    @CsvSource({
            "ADOBECODEBANC,ABC,BANC",
            "a,a,a",
            "a,aa,''",
    })
    void minWindowTest(String s, String t, String expected) {
        Assertions.assertEquals(expected, new Solution76().minWindow(s, t));
    }

    @Test
    void combineTest() {
        System.out.printf("%s\n", new Solution77().combine(4, 2));
    }

    @Test
    void subsetsTest() {
        System.out.printf("%s\n", new Solution78().subsets(new int[]{1, 2, 3}));
    }

    @ParameterizedTest
    @MethodSource("provideArraysForExist")
    void existTest(char[][] board, String word, boolean expected) {
        Assertions.assertEquals(expected, new Solution79().exist(board, word));
    }

    @ParameterizedTest
    @CsvSource({
            "'2,5,6,0,0,1,2',0,true",
            "'2,5,6,0,0,1,2',1,true",
            "'2,5,6,0,0,1,2',2,true",
            "'2,5,6,0,0,1,2',3,false",
            "'2,5,6,0,0,1,2',4,false",
            "'2,5,6,0,0,1,2',5,true",
            "'2,5,6,0,0,1,2',6,true",
            "'2,5,6,0,0,1,2',7,false",
            "'1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1',2,true",
            "'4,5,6,7,0,1,2',0,true",
            "'4,5,6,7,0,1,2',5,true",
    })
    void testSearch(String nums, int target, boolean expected) {
        Assertions.assertEquals(expected, new Solution81().search(Arrays.stream(nums.split(",")).mapToInt(Integer::valueOf).toArray(), target));
    }

    @ParameterizedTest
    @CsvSource({
            // "'1,2,3,3,4,4,5','1,2,5'",
            "'1,1',''",
    })
    void deleteDuplicatesTest(String nums, String expected) {
        ListNode head = null;
        ListNode idx = null;
        for (String s : nums.split(",")) {
            if (Objects.isNull(head)) {
                idx = head = new ListNode(Integer.parseInt(s), null);
            } else {
                idx.next = new ListNode(Integer.parseInt(s), null);
                idx = idx.next;
            }
        }
        ListNode newHead = new Solution82().deleteDuplicates(head);
        StringBuilder sb = new StringBuilder();
        while (newHead != null) {
            sb.append(newHead.val);
            if (newHead.next != null) {
                sb.append(",");
            }
            newHead = newHead.next;
        }
        Assertions.assertEquals(expected, sb.toString());
    }
}

class Solution82 {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode preNode = null;
        ListNode lastVal = head;
        ListNode idx = head;
        head = null;
        boolean repeat = false;
        while (idx != null) {
            if (lastVal.val != idx.val) {
                if (!repeat) {
                    if (Objects.isNull(preNode)) {
                        head = preNode = lastVal;
                    } else {
                        preNode.next = lastVal;
                        preNode = lastVal;
                    }
                }
                lastVal.next = null;
                lastVal = idx;
                repeat = false;
            } else if (lastVal != idx) {
                repeat = true;
            }
            idx = idx.next;
        }
        if (!repeat) {
            if (Objects.isNull(preNode)) {
                head = lastVal;
            } else {
                preNode.next = lastVal;
            }
        }
        return head;
    }

    public ListNode deleteDuplicates1(ListNode head) {
        Map<Integer, Integer> valCount = new LinkedHashMap<>();
        while (head != null) {
            final int val = head.val;
            valCount.compute(val, (k, v) -> {
                if (Objects.isNull(v)) {
                    return 1;
                } else {
                    return v + 1;
                }
            });
            head = head.next;
        }

        ListNode newHead = null;
        ListNode idx = null;
        for (Map.Entry<Integer, Integer> e : valCount.entrySet()) {
            if (e.getValue() == 1) {
                if (Objects.isNull(newHead)) {
                    idx = newHead = new ListNode(e.getKey(), null);
                } else {
                    idx.next = new ListNode(e.getKey(), null);
                    idx = idx.next;
                }
            }
        }
        return newHead;
    }
}

class Solution83 {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode lastVal = head;
        ListNode idx = head;
        while (idx != null) {
            if (lastVal.val != idx.val) {
                lastVal.next = idx;
                lastVal = idx;
            } else if (lastVal != idx) {
                lastVal.next = null;
            }
            idx = idx.next;
        }
        return head;
    }
}

class Solution81 {
    private boolean searchNormal(int[] nums, int target, int startIncludeIdx, int endExcludeIdx) {
        while (startIncludeIdx < endExcludeIdx) {
            final int midIdx = (startIncludeIdx + endExcludeIdx) / 2;
            if (nums[midIdx] == target) {
                return true;
            }

            if (nums[midIdx] > target) {
                endExcludeIdx = midIdx;
            } else {
                startIncludeIdx = midIdx + 1;
            }
        }
        return false;
    }

    private boolean searchTurning(int[] nums, int target, int startIncludeIdx, int endExcludeIdx) {
        if (startIncludeIdx < endExcludeIdx) {
            final int midIdx = (startIncludeIdx + endExcludeIdx) / 2;
            final int mid = nums[midIdx];
            if (mid == target) {
                return true;
            }

            final int start = nums[startIncludeIdx];
            final int end = nums[endExcludeIdx - 1];
            if (start == target || end == target) {
                return true;
            }

            if (mid < start) {
                if (target < end && target > mid) {
                    return searchNormal(nums, target, midIdx + 1, endExcludeIdx);
                }
                return searchTurning(nums, target, startIncludeIdx + 1, midIdx);
            }

            if (mid > end) {
                if (target > start && target < mid) {
                    return searchNormal(nums, target, startIncludeIdx + 1, midIdx);
                }
                return searchTurning(nums, target, midIdx + 1, endExcludeIdx);
            }

            if (mid == start && mid == end) {
                return searchTurning(nums, target, startIncludeIdx + 1, midIdx) ||
                        searchTurning(nums, target, midIdx + 1, endExcludeIdx);
            }

            return searchNormal(nums, target, startIncludeIdx + 1, endExcludeIdx - 1);
        }
        return false;
    }

    public boolean search(int[] nums, int target) {
        int startIdxIncluded = 0;
        int endIdxExcluded = nums.length;
        return searchTurning(nums, target, startIdxIncluded, endIdxExcluded);
    }
}

class Solution80 {
    public int removeDuplicates(int[] nums) {
        int lastVal = nums[0], end = 1;
        int lastIdx = 1;
        int count = 0;
        while (end < nums.length) {
            if (nums[end] != lastVal) {
                count = 0;
                lastVal = nums[end];
            }

            if (count++ < 2) {
                nums[lastIdx++] = nums[end++];
            }
        }
        return lastIdx;
    }
}


class Solution79 {
    public boolean exist(char[][] board, String word) {
        final int rows = board.length;
        final int cols = board[0].length;
        final char[] chars = word.toCharArray();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (backTrace(board, chars, row, col, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean backTrace(char[][] board, char[] chars, int row, int col, int wordIdx) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || board[row][col] == '.') {
            return false;
        }

        if (board[row][col] == chars[wordIdx]) {
            char c = board[row][col];
            board[row][col] = '.';
            final int nextWordIdx = wordIdx + 1;
            if (nextWordIdx >= chars.length) {
                return true;
            }

            if (backTrace(board, chars, row - 1, col, nextWordIdx) ||
                    backTrace(board, chars, row + 1, col, nextWordIdx) ||
                    backTrace(board, chars, row, col - 1, nextWordIdx) ||
                    backTrace(board, chars, row, col + 1, nextWordIdx)) {
                return true;
            }
            board[row][col] = c;
        }

        return false;
    }
}

class Solution78 {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        List<Integer> list = new ArrayList<>();
        backTrace(result, list, 0, nums);
        return result;
    }

    private void backTrace(List<List<Integer>> result, List<Integer> list, int from, int[] nums) {
        if (from == nums.length) {
            return;
        }

        for (int i = from; i < nums.length; i++) {
            final Integer num = nums[i];
            list.add(num);
            result.add(new ArrayList<>(list));
            backTrace(result, list, i + 1, nums);
            list.remove(num);
        }
    }
}

class Solution77 {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        int flag = 0;
        backTrace(result, list, n, 0, k);
        return result;
    }

    private void backTrace(List<List<Integer>> result, List<Integer> list, int n, int from, int k) {
        if (k == 0) {
            result.add(new ArrayList<>(list));
            return;
        }

        if (n - from < k) {
            return;
        }

        for (int i = from; i < n; i++) {
            list.add(i + 1);
            backTrace(result, list, n, i + 1, k - 1);
            list.remove(list.size() - 1);
        }
    }

    private void backTrace(List<List<Integer>> result, List<Integer> list, int flag, int n, int from, int k) {
        for (int i = from; i < n; i++) {
            if ((flag & 1 << i) == 0) {
                list.add(i + 1);
                int nextK = k - 1;
                if (nextK > 0) {
                    backTrace(result, list, (flag | (1 << i)), n, i, k - 1);
                } else {
                    result.add(new ArrayList<>(list));
                }
                list.remove(Integer.valueOf(i + 1));
            }
        }
    }
}

class Solution76 {
    public String minWindow(String s, String t) {
        if (t.length() > s.length()) {
            return "";
        }

        int[] targetCharCounts = new int[52];
        long targetFlag = 0;
        int[] matchCharCounts = new int[52];

        for (char c : t.toCharArray()) {
            targetFlag = updateRecode(c, targetCharCounts, targetFlag);
        }

        int lastStart = -1, lastEnd = -1;
        int start = 0, end = 0;
        long matchFlag = targetFlag;
        while (end < s.length()) {
            char c = s.charAt(end);
            matchFlag = updateMatchRecode(c, targetCharCounts, matchCharCounts, matchFlag, targetFlag, true);
            if (matchFlag == 0) {
                do {
                    c = s.charAt(start++);
                    matchFlag = updateMatchRecode(c, targetCharCounts, matchCharCounts, matchFlag, targetFlag, false);
                } while (matchFlag == 0);
                if (lastStart == -1) {
                    lastStart = start - 1;
                    lastEnd = end;
                } else if (end - start + 1 < lastEnd - lastStart) {
                    lastStart = start - 1;
                    lastEnd = end;
                }
            }
            end++;
        }

        return (lastStart != -1) ? s.substring(lastStart, lastEnd + 1) : "";
    }

    private long updateRecode(char c, int[] targetCharCounts, long flag) {
        int lowerCaseIdx = c - 'a';
        int upperCaseIdx = c - 'A';
        if (lowerCaseIdx >= 0 && lowerCaseIdx < 26) {
            targetCharCounts[lowerCaseIdx] += 1;
            return flag | 1 << lowerCaseIdx;
        }
        if (upperCaseIdx >= 0 && upperCaseIdx < 26) {
            targetCharCounts[upperCaseIdx + 26] += 1;
            return flag | 1L << (upperCaseIdx + 26);
        }
        throw new IllegalArgumentException();
    }

    private long updateMatchRecode(char c, int[] targetCharCounts, int[] matchCharCounts, long matchFlag, long targetFlag, boolean plus) {
        int lowerCaseIdx = c - 'a';
        int upperCaseIdx = c - 'A';
        if (lowerCaseIdx >= 0 && lowerCaseIdx < 26 && (targetFlag & (1 << lowerCaseIdx)) > 0) {
            matchCharCounts[lowerCaseIdx] += plus ? 1 : -1;
            return matchCharCounts[lowerCaseIdx] >= targetCharCounts[lowerCaseIdx] ? matchFlag & ~(1 << lowerCaseIdx) : matchFlag | (1 << lowerCaseIdx);
        }
        if (upperCaseIdx >= 0 && upperCaseIdx < 26 && (targetFlag & (1L << (upperCaseIdx + 26))) > 0) {
            matchCharCounts[upperCaseIdx + 26] += plus ? 1 : -1;
            return matchCharCounts[upperCaseIdx + 26] >= targetCharCounts[upperCaseIdx + 26] ? matchFlag & ~(1L << (upperCaseIdx + 26)) : matchFlag | (1L << (upperCaseIdx + 26));
        }
        return matchFlag;
    }
}

class Solution75 {
    public void sortColors(int[] nums) {
        final int redVal = 0;
        final int whiteVal = 1;
        final int blueVal = 2;
        Integer lastWhiteIdx = null;

        int leftIdx = 0;
        int rightIdx = nums.length;

        while (leftIdx < rightIdx) {
            if (nums[leftIdx] == blueVal) {
                while (rightIdx - 1 > leftIdx && nums[rightIdx - 1] == blueVal) {
                    rightIdx--;
                }
                if (rightIdx > leftIdx) {
                    nums[leftIdx] = nums[rightIdx - 1];
                    nums[rightIdx - 1] = blueVal;
                }
                rightIdx--;
            } else if (nums[leftIdx] == whiteVal) {
                if (Objects.isNull(lastWhiteIdx)) {
                    lastWhiteIdx = leftIdx;
                }
                leftIdx++;
            } else {
                if (Objects.nonNull(lastWhiteIdx)) {
                    final int lastIdx = lastWhiteIdx;
                    nums[lastIdx] = redVal;
                    nums[leftIdx] = whiteVal;
                    lastWhiteIdx = lastIdx + 1;
                }
                leftIdx++;
            }
        }
    }
}

class Solution74 {
    public boolean searchMatrix(int[][] matrix, int target) {
        final int rows = matrix.length;
        final int cols = matrix[0].length;
        final int totalCount = rows * cols;

        for (int min = 0, max = totalCount; min < max; ) {
            int mid = (min + max) / 2;
            final int col = mid % cols;
            final int row = mid / cols;
            if (matrix[row][col] == target) {
                return true;
            } else if (target < matrix[row][col]) {
                max = mid;
            } else {
                min = mid + 1;
            }
        }
        return false;
    }
}


class Solution73 {
    public void setZeroes(int[][] matrix) {
        final int rows = matrix.length;
        final int cols = matrix[0].length;
        final int totalCount = rows * cols;
        setZeroes(matrix, 0, rows, cols, totalCount);
    }

    private void setZeroes(int[][] matrix, int idx, int rows, int cols, int totalCount) {
        while (idx < totalCount) {
            int row = idx / cols;
            int col = idx % cols;

            if (0 == matrix[row][col]) {
                setZeroes(matrix, idx + 1, rows, cols, totalCount);
                Arrays.fill(matrix[row], 0);
                for (int rowIdx = 0; rowIdx < matrix.length; rowIdx++) {
                    matrix[rowIdx][col] = 0;
                }
                break;
            } else {
                idx++;
            }
        }
    }
}

class Solution72 {
    public int minDistance(String word1, String word2) {
        final int word1Length = word1.length();
        final int word2Length = word2.length();
        if (0 == word1Length || 0 == word2Length) {
            return Math.max(word1Length, word2Length);
        }

        final int[][] state = new int[word1Length][word2Length];
        for (int i = 0; i < word1Length; i++) {
            for (int j = 0; j < word2Length; j++) {
                state[i][j] = -1;
            }
        }
        return dp2(word1, word1Length - 1, word2, word2Length - 1, state);
        // dp(word1, 0, word2, 0, state);
        // return state[word1Length - 1][word2Length - 1];
    }

    private int dp2(String word1, int idx1, String word2, int idx2, int[][] state) {
        if (-1 != state[idx1][idx2]) {
            return state[idx1][idx2];
        }

        final boolean charEqual = word1.charAt(idx1) == word2.charAt(idx2);
        if (0 == idx1 && 0 == idx2) {
            state[idx1][idx2] = charEqual ? 0 : 1;
        } else if (0 == idx1) {
            state[idx1][idx2] = charEqual ? idx2 : dp2(word1, idx1, word2, idx2 - 1, state) + 1;
        } else if (0 == idx2) {
            state[idx1][idx2] = charEqual ? idx1 : dp2(word1, idx1 - 1, word2, idx2, state) + 1;
        } else if (charEqual) {
            state[idx1][idx2] = dp2(word1, idx1 - 1, word2, idx2 - 1, state);
        } else {
            state[idx1][idx2] = Math.min(Math.min(dp2(word1, idx1 - 1, word2, idx2, state), dp2(word1, idx1, word2, idx2 - 1, state)),
                    dp2(word1, idx1 - 1, word2, idx2 - 1, state)) + 1;
        }

        return state[idx1][idx2];
    }

    private void dp(String word1, int idx1, String word2, int idx2, int[][] state) {
        if (idx1 >= word1.length() || idx2 >= word2.length()) {
            return;
        }

        final boolean charEqual = word1.charAt(idx1) == word2.charAt(idx2);
        if (0 == idx1 && 0 == idx2) {
            state[idx1][idx2] = charEqual ? 0 : 1;
        } else if (0 == idx1) {
            state[idx1][idx2] = charEqual ? idx2 : state[idx1][idx2 - 1] + 1;
        } else if (0 == idx2) {
            state[idx1][idx2] = charEqual ? idx1 : state[idx1 - 1][idx2] + 1;
        } else if (charEqual) {
            state[idx1][idx2] = state[idx1 - 1][idx2 - 1];
        } else {
            state[idx1][idx2] = Math.min(Math.min(state[idx1 - 1][idx2], state[idx1][idx2 - 1]), state[idx1 - 1][idx2 - 1]) + 1;
        }

        idx2 = idx2 + (idx1 + 1) / word1.length();
        idx1 = (idx1 + 1) % word1.length();
        dp(word1, idx1, word2, idx2, state);
    }
}

class Solution71 {
    public String simplifyPath(String path) {
        Stack<String> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length(); i++) {
            final char c = path.charAt(i);
            if (c == '/') {
                final int length = sb.length();
                if (length == 1 && sb.charAt(0) == '.') {
                    sb = new StringBuilder();
                    continue;
                }
                if (length == 2 && sb.charAt(0) == '.' && sb.charAt(1) == '.') {
                    if (!stack.isEmpty()) stack.pop();
                    sb = new StringBuilder();
                    continue;
                }
                if (length > 0) {
                    stack.push(sb.toString());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(c);
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            result.append("/").append(stack.get(i));
        }

        if (sb.length() > 0) {
            result.append(sb);
        }

        return result.toString();
    }
}


class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

class Solution61 {
    public ListNode rotateRight(ListNode head, int k) {
        if (Objects.isNull(head)) {
            return head;
        }

        if (0 == k) {
            return head;
        }

        ListNode idx = head;
        int length = 1;
        if (head.next != null) {
            do {
                length++;
                idx = idx.next;
            } while (idx.next != null);
        }
        final ListNode tail = idx;

        k = k % length;

        if (0 == k) {
            return head;
        }

        ListNode kIdx = head;
        ListNode lastIdx = null;
        int count = 0;
        do {
            count++;
            lastIdx = kIdx;
            kIdx = kIdx.next;
        } while (count < length - k);

        lastIdx.next = null;
        tail.next = head;
        head = kIdx;

        return head;
    }
}

class Solution60 {
    public String getPermutation(int n, int k) {
        int[] nFloor = new int[n];
        initFloor(nFloor);
        int[] used = new int[n];
        char[] c = new char[n];

        int kIdx = k - 1;
        for (int i = n - 1; i >= 0; i--) {
            int idx = i == 0 ? 0 : kIdx / nFloor[i - 1];
            kIdx = (i == 0 ? 0 : kIdx % nFloor[i - 1]);
            int val = 0;
            for (int j = 0; j < used.length; j++) {
                if (used[j] > 0) continue;
                if (idx > 0) {
                    idx--;
                } else {
                    val = j;
                    used[j] = 1;
                    break;
                }
            }
            c[n - 1 - i] = (char) (val + '1');
        }
        return new String(c);
    }

    /**
     * 1,2,6,24,120...
     *
     * @param nFloor
     * @return
     */
    private void initFloor(int[] nFloor) {
        nFloor[0] = 1;
        for (int i = 1; i < nFloor.length; i++) {
            nFloor[i] = nFloor[i - 1] * (i + 1);
        }
    }
}

class Solution59 {
    public int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        int minRow = 0;
        int maxRow = n;
        int minCol = 0;
        int maxCol = n;
        int i = 1;
        while (minCol < maxCol && minRow < maxRow) {
            for (int col = minCol; col < maxCol; col++) {
                matrix[minRow][col] = i++;
            }

            if (minRow + 1 >= maxRow) break;
            for (int row = minRow + 1; row < maxRow; row++) {
                matrix[row][maxCol - 1] = i++;
            }

            if (minCol + 1 >= maxCol) break;
            for (int col = maxCol - 2; col >= minCol; col--) {
                matrix[maxRow - 1][col] = i++;
            }

            for (int row = maxRow - 2; row > minRow; row--) {
                matrix[row][minCol] = i++;
            }
            minCol++;
            maxCol--;
            minRow++;
            maxRow--;
        }
        return matrix;
    }
}

class Solution58 {
    public int lengthOfLastWord(String s) {
        boolean find = false;
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) != ' ') {
                if (!find) {
                    find = true;
                }
                count++;
            } else if (find) {
                break;
            }
        }
        return count;
    }
}


class Solution57 {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        if (intervals.length == 0) {
            return new int[][]{newInterval};
        }

        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        final int[] leftRst = binarySearch(intervals, 0, intervals.length, newInterval[0]);
        final int[] rightRst = binarySearch(intervals, 0, intervals.length, newInterval[1]);

        int[][] rst = new int[intervals.length - (rightRst[1] == 0 ? rightRst[0] - leftRst[0] - 1 : rightRst[0] - leftRst[0])][2];
        int nIdx = 0;
        for (int i = 0; i < leftRst[0]; i++) {
            rst[nIdx++] = intervals[i];
        }
        int[] mergeInterval = new int[]{leftRst[0] >= intervals.length ? newInterval[0] : Math.min(intervals[leftRst[0]][0], newInterval[0]),
                rightRst[1] == 0 ? newInterval[1] : Math.max(newInterval[1], intervals[rightRst[0]][1])};
        rst[nIdx++] = mergeInterval;
        for (int i = (rightRst[1] == 0 ? rightRst[0] : rightRst[0] + 1); i < intervals.length; i++) {
            rst[nIdx++] = intervals[i];
        }
        return rst;
    }

    private int[] binarySearch(int[][] intervals, int from, int to, int val) {
        int mid;
        while ((mid = (from + to) / 2) >= from && mid < to) {
            final int[] intervalMid = intervals[mid];
            if (intervalMid[0] > val) {
                to = mid;
                continue;
            }

            if (intervalMid[1] >= val) {
                return new int[]{mid, 1};
            }

            from = mid + 1;
        }
        return new int[]{mid, 0};
    }
}

class Solution56 {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        int[][] result = new int[intervals.length][2];
        int[] lastArr = intervals[0];
        int count = 0;
        for (int i = 1; i < intervals.length; i++) {
            if (lastArr[1] < intervals[i][0]) {
                result[count] = lastArr;
                lastArr = intervals[i];
                count++;
            } else {
                lastArr[1] = Math.max(lastArr[1], intervals[i][1]);
            }
        }

        result[count] = lastArr;
        return Arrays.copyOfRange(result, 0, count + 1);
    }
}

class Solution55 {
    public boolean canJump(int[] nums) {
        int[] nextJump = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            nextJump[i] = -1;
        }
        return canJumpGreed(nums, 0, nextJump);
    }

    public boolean canJumpGreed(int[] nums, int idx, int[] nextJump) {
        if (idx >= nums.length - 1) {
            return true;
        }

        if (nextJump[idx] >= 0) {
            return false;
        }

        int nextMaxJump = nums[idx] + idx;
        nextJump[idx] = nextMaxJump;
        for (int candidate = nextMaxJump; candidate > idx; candidate--) {
            if (canJumpGreed(nums, candidate, nextJump)) {
                return true;
            }
        }

        return false;
    }

    public boolean canJumpDp(int[] nums) {
        int maxJump = nums[0];
        for (int i = 1; i < nums.length - 1; i++) {
            if (maxJump < i) {
                break;
            }
            maxJump = Math.max(maxJump, nums[i] + i);
        }
        return maxJump >= nums.length - 1;
    }
}

class Solution54 {
    public List<Integer> spiralOrder(int[][] matrix) {
        int minRow = 0;
        int maxRow = matrix.length;
        int minCol = 0;
        int maxCol = matrix[0].length;
        List<Integer> result = new ArrayList<>(maxRow * maxCol);

        while (minCol < maxCol && minRow < maxRow) {
            for (int col = minCol; col < maxCol; col++) {
                result.add(matrix[minRow][col]);
            }

            if (minRow + 1 >= maxRow) break;
            for (int row = minRow + 1; row < maxRow; row++) {
                result.add(matrix[row][maxCol - 1]);
            }

            if (minCol + 1 >= maxCol) break;
            for (int col = maxCol - 2; col >= minCol; col--) {
                result.add(matrix[maxRow - 1][col]);
            }

            for (int row = maxRow - 2; row > minRow; row--) {
                result.add(matrix[row][minCol]);
            }
            minCol++;
            maxCol--;
            minRow++;
            maxRow--;
        }
        return result;
    }
}
