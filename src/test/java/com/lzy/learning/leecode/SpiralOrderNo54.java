package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class SpiralOrderNo54 {
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
