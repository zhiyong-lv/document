package com.lzy.learning.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LeeCodeTest {



    @Test
    void isValidSudokuTest() {
        char[][] board = new char[][]{{'5', '3', '.', '.', '7', '.', '.', '.', '.'}, {'6', '.', '.', '1', '9', '5', '.', '.', '.'}, {'.', '9', '8', '.', '.', '.', '.', '6', '.'}, {'8', '.', '.', '.', '6', '.', '.', '.', '3'}, {'4', '.', '.', '8', '.', '3', '.', '.', '1'}, {'7', '.', '.', '.', '2', '.', '.', '.', '6'}, {'.', '6', '.', '.', '.', '.', '2', '8', '.'}, {'.', '.', '.', '4', '1', '9', '.', '.', '5'}, {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};
        isValidSudoKu(board);
    }

    int validLength = 9;
    int mask = 0;

    {
        IntStream.range(0, 9).forEach(i -> mask |= 1 << i);
    }

    public boolean isValidSudoKu(char[][] board) {
        if (!isValidRequiredSize(board, validLength, validLength)) {
            return false;
        }

        int[] matched = new int[9];
        int rowMatchedStart = 0;
        int colMatchedStart = '9' - '1' + 1 + rowMatchedStart;
        int zoneMatchedStart = '9' - '1' + 1 + colMatchedStart;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                char c = board[row][col];
                if (c - '.' == 0) {
                    continue;
                }

                int index = c - '1';
                if (index < 0 || index > '9' - '1') {
                    return false;
                }

                int rowMatchedBit = 1 << (index + rowMatchedStart);
                int rowBitmap = matched[row];
                if ((rowBitmap & rowMatchedBit) > 0) {
                    return false;
                } else {
                    matched[row] |= rowMatchedBit;
                }

                int colMatchedBit = 1 << (index + colMatchedStart);
                int colBitmap = matched[col];
                if ((colBitmap & colMatchedBit) > 0) {
                    return false;
                } else {
                    matched[col] |= colMatchedBit;
                }
                int zoneMatchedBit = 1 << (index + zoneMatchedStart);
                int zoneBitMapIndex = (row / 3) * 3 + (col / 3);
                if ((matched[zoneBitMapIndex] & zoneMatchedBit) > 0) {
                    return false;
                } else {
                    matched[zoneBitMapIndex] |= zoneMatchedBit;
                }
            }
        }

        return true;
    }

    public boolean isValidSudoku1(char[][] board) {
        if (!isValidRequiredSize(board, validLength, validLength)) {
            return false;
        }

        return IntStream.range(0, 9).allMatch(i -> isValidRequiredZone(board, i, i + 1, 0, validLength)) &&
                IntStream.range(0, 9).allMatch(i -> isValidRequiredZone(board, 0, validLength, i, i + 1)) &&
                Stream.of(0, 3, 6).allMatch(row -> Stream.of(0, 3, 6).allMatch(col -> isValidRequiredZone(board, row, row + 3, col, col + 3)));
    }

    private boolean isValidRequiredSize(char[][] board, int requiredRow, int requiredCol) {
        return board.length == requiredRow && Arrays.stream(board).allMatch(arr -> arr.length == requiredCol);
    }

    private boolean isValidRequiredZone(char[][] board, int startRow, int endRow, int startCol, int endCol) {
        int bitmap = 0;
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                char c = board[row][col];
                if (c - '.' == 0) {
                    continue;
                }
                int bit = 1 << (c - '1');
                if ((mask & bit) == 0 || (bitmap & bit) > 0) {
                    return false;
                }
                bitmap |= bit;
            }
        }
        return true;
    }

    @Test
    void searchRangeTest() {
        int[] nums = new int[]{5, 7, 7, 8, 8, 10};
        int target = 8;
        int[] expectedRst = new int[]{3, 4};
//        int[] nums = new int[] {2,2};
//        int target = 3;
//        int [] expectedRst = new int[] {-1,-1};
        int[] result = searchRange(nums, target);
        Assertions.assertArrayEquals(expectedRst, result);
    }

    public int[] searchRange(int[] nums, int target) {
        int length = nums.length;
        int[] result = new int[]{-1, -1};

        if (length == 0) {
            return result;
        }

        if (length == 1) {
            if (nums[0] == target) {
                result[0] = result[1] = 0;
            }
            return result;
        }

        for (int nextStart = 0, nextEnd = nums.length - 1, mid = (nextStart + nextEnd) / 2; nextStart <= nextEnd; mid = (nextStart + nextEnd) / 2) {
            if (nums[mid] == target) {
                return expandRange(nums, mid);
            }

            if ((nextStart + nextEnd) / 2 == nextStart) {
                if (nums[nextEnd] == target) {
                    result[0] = nextEnd;
                    result[1] = nextEnd;
                }
                return result;
            }

            if (nums[mid] > target) {
                // go to left part
                nextEnd = mid - 1;
            } else {
                // go to right part
                nextStart = mid + 1;
            }
        }

        return result;
    }

    private int[] expandRange(int[] nums, int index) {
        int target = nums[index];
        int start = index, end = index;
        while (start - 1 >= 0 && nums[start - 1] == target) {
            start--;
        }

        while (end + 1 < nums.length && nums[end + 1] == target) {
            end++;
        }

        return new int[]{start, end};
    }

    @Test
    void testSearch() {
        int[] nums = new int[]{4, 5, 6, 7, 0, 1, 2};
//        int[] nums = new int[]{1,3};
//        int[] nums = new int[]{8, 9, 2, 3, 4};
        Map<Integer, Integer> maps = new HashMap<>();
//        maps.put(9, 1);
//        maps.put(1,0);
        maps.put(0, 4);
        maps.put(3, -1);
        maps.entrySet().forEach(e -> Assertions.assertEquals(e.getValue(), search(nums, e.getKey())));
    }


    public int search(int[] nums, int target) {
        return search(nums, 0, nums.length - 1, target);
    }

    private int search(int[] nums, final int start, final int end, final int target) {
        if (start < 0 || end >= nums.length || end < start) {
            return -1;
        }

        if (end == start) {
            return nums[start] == target ? start : -1;
        }

        boolean revertIndexFound = false;
        for (int nextStart = start, nextEnd = end, i = (nextEnd + nextStart) / 2; nextEnd >= nextStart && nextStart >= 0 && nextEnd < nums.length; i = (nextEnd + nextStart) / 2) {
            if (nums[i] == target) {
                return i;
            }
            if (nums[i] > target) {
                int tempNextEnd = i - 1;
                int tempIndex = (tempNextEnd + nextStart) / 2;
                if (nums[tempIndex] > nums[i]) {
                    nextStart = tempIndex + 1;
                    revertIndexFound = true;
                }
                nextEnd = i - 1;
            } else {
                int tempNextStart = i + 1;
                int tempIndex = (nextEnd + tempNextStart) / 2;
                if (nums[tempIndex] < nums[i]) {
                    nextEnd = tempIndex - 1;
                    revertIndexFound = true;
                }
                nextStart = i + 1;
            }
        }

        if (revertIndexFound) {
            return -1;
        }

        int mid = (start + end) / 2;
        if (nums[mid] < target) {
            return search(nums, start, mid - 1, target);
        } else {
            return search(nums, mid + 1, end, target);
        }
    }

    /**
     * @param nums   nums array
     * @param start  include
     * @param end    exclude
     * @param target num to find
     * @return index of num, or -1
     */
    private int search1(int[] nums, int start, int end, int target) {
        if (start < 0 || end > nums.length || end < 0 || start >= nums.length) {
            return -1;
        }

        if (end - start == 0 || end - start == 1) {
            return target == nums[start] ? start : -1;
        }

        int midThisRound = (start + end) / 2;

        if (target < nums[midThisRound]) {
            int normalSearch = search(nums, start, midThisRound, target);
            if (normalSearch >= 0) {
                return normalSearch;
            }
            if (midThisRound + 1 > end) {
                return -1;
            } else {
                return search(nums, midThisRound + 1, end, target);
            }
        } else {
            int normalSearch = search(nums, midThisRound, end, target);
            if (normalSearch >= 0) {
                return normalSearch;
            }
            if (midThisRound - 1 < start) {
                return -1;
            } else {
                return search(nums, start, midThisRound - 1, target);
            }
        }
    }

    @ParameterizedTest
    @CsvSource({"'',0", "((),2", "()((),2", ")()()),4", "(()()),6", ")(((((()())()()))()(()))(,22", ")()(((())))(,10", ")(())))(())()),6",})
    void longestValidParenthesesTest(String s, int expected) {
        Assertions.assertEquals(expected, longestValidParentheses(s));
    }

    public int longestValidParentheses(String s) {
        if (Objects.isNull(s) || s.isEmpty()) {
            return 0;
        }

        int[] dp = new int[s.length()];
        int[] end = new int[s.length()];

        dp[0] = 0;
        end[0] = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    if (i - 2 > 0 && s.charAt(i - 2) == ')') {
                        end[i] = end[i - 2] + 2;
                    } else {
                        end[i] = 2;
                    }
                } else if (s.charAt(i - 1) == ')') {
                    end[i] = end[i - 1];
                    if (i - end[i] - 1 >= 0 && s.charAt(i - end[i] - 1) == '(') {
                        end[i] += 2;
                        while (i - end[i] >= 0 && s.charAt(i - end[i]) == ')' && end[i - end[i]] != 0) {
                            end[i] += end[i - end[i]];
                        }
                    } else {
                        end[i] = 0;
                    }
                }
            } else {
                end[i] = 0;
            }
            dp[i] = Math.max(dp[i - 1], end[i]);
        }

        return dp[s.length() - 1];
    }

    class MatchParentheses implements Comparable<MatchParentheses> {
        int left = -1;
        int right = -1;

        public MatchParentheses() {
        }

        public MatchParentheses(int left) {
            this.left = left;
        }

        public MatchParentheses(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public boolean start() {
            return left >= 0;
        }

        public boolean matched() {
            return right > 0;
        }

        public int getLength() {
            return (right >= 1 && left >= 0) ? right - left + 1 : 0;
        }

        @Override
        public int compareTo(MatchParentheses o) {
            return getLength() - o.getLength();
        }

        @Override
        public boolean equals(Object o) {
            if (Objects.isNull(o) || o.getClass() != getClass()) {
                return false;
            }

            MatchParentheses other = (MatchParentheses) o;
            return getLength() - other.getLength() == 0;
        }

        @Override
        public int hashCode() {
            return getLength();
        }
    }

    public class MatchParenthesesStack extends Stack<MatchParentheses> {
        private MatchParentheses max = new MatchParentheses();

        @Override
        public MatchParentheses push(MatchParentheses item) {
            if (empty()) {
                if (max.compareTo(item) < 0) {
                    max = item;
                }
                return super.push(item);
            }
            MatchParentheses peek = peek();
            if (peek.matched() && item.matched() && peek.right + 1 == item.left) {
                peek.right = item.right;
                if (max.compareTo(peek) < 0) {
                    max = peek;
                }
            } else {
                super.push(item);
                if (max.compareTo(item) < 0) {
                    max = item;
                }
            }
            return item;
        }
    }

    public int longestValidParentheses2(String s) {
        MatchParenthesesStack stack = new MatchParenthesesStack();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                MatchParentheses match = new MatchParentheses(i);
                stack.push(match);
            } else {
                MatchParentheses match = null;
                Stack<MatchParentheses> temp = new Stack<>();
                while (!stack.empty()) {
                    match = stack.pop();
                    if (!match.matched()) {
                        break;
                    }
                    temp.push(match);
                    match = null;
                }

                if (Objects.nonNull(match)) {
                    match.right = i;
                    stack.push(match);
                } else {
                    while (!temp.isEmpty()) {
                        stack.push(temp.pop());
                    }

                    while ((i + 1) < s.length() && s.charAt(i + 1) == ')') {
                        i++;
                    }
                }
            }
        }

        if (stack.size() == 0) {
            return 0;
        }

        return stack.max.getLength();
    }
}
