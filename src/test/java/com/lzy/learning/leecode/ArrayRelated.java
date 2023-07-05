package com.lzy.learning.leecode;

public class ArrayRelated {
}

class Solution704 {
    public int search(int[] nums, int target) {
        int min = 0;
        int max = nums.length - 1;

        while (min <= max) {
            int mid = min + (max - min) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                max = mid - 1;
            } else {
                min = mid + 1;
            }
        }

        return -1;
    }
}

class Solution27 {
    public int removeElement(int[] nums, int val) {
        int newLen = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[newLen++] = nums[i];
            }
        }
        return newLen;
    }
}

class Solution977 {
    public int[] sortedSquares(int[] nums) {
        int[] numsCopy = new int[nums.length];

        for (int i = 0; i < nums.length; i++) {
            numsCopy[i] = nums[i] * nums[i];
        }

        int leftIdx = 0;
        int rightIdx = nums.length - 1;

        for (int i = nums.length - 1; i >= 0; i--) {
            if (numsCopy[leftIdx] > numsCopy[rightIdx]) {
                nums[i] = numsCopy[leftIdx++];
            } else {
                nums[i] = numsCopy[rightIdx--];
            }
        }

        return nums;
    }
}

class Solution209 {
    public static void main(String[] args) {
        System.out.println(new Solution209().minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3}));
    }

    public int minSubArrayLen(int target, int[] nums) {
        int start = 0, end = 0, sum = 0;
        int minLength = Integer.MAX_VALUE;

        while (true) {
            if (sum < target && end < nums.length) {
                sum += nums[end++];
            } else if (sum >= target && start < nums.length) {
                minLength = Math.min(minLength, end - start);
                sum -= nums[start++];
            } else if ((sum < target & end == nums.length) || start == nums.length) {
                break;
            } else {
                System.out.printf("start is %d, end is %d, sum is %d%n", start, end, sum);
            }
        }

        return (minLength == Integer.MAX_VALUE) ? 0 : minLength;
    }
}

class Solution59new {

    public static void main(String[] args) {
        final int[][] rst = new Solution59new().generateMatrix(4);
        for (int i = 0; i < rst.length; i++) {
            for (int j = 0; j < rst[i].length; j++) {
                System.out.printf("%d,", rst[i][j]);
            }
            System.out.println("");
        }
    }
    int[][] stepMatrix = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public int[][] generateMatrix(final int n) {
        int stepMatrixIdx = 0;
        int[][] rst = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rst[i][j] = 0;
            }
        }

        int row = 0, col = 0;
        for (int val = 1; val <= n * n; val++) {
            if (rst[row][col] == 0) {
                rst[row][col] = val;
            } else {
                break;
            }

            final int nextRow = row + stepMatrix[stepMatrixIdx][0];
            final int nextCol = col + stepMatrix[stepMatrixIdx][1];
            if (nextRow < 0 || nextRow >= n || nextCol < 0 || nextCol >= n || rst[nextRow][nextCol] > 0) {
                stepMatrixIdx = (stepMatrixIdx + 1) % stepMatrix.length;
            }

            row += stepMatrix[stepMatrixIdx][0];
            col += stepMatrix[stepMatrixIdx][1];
        }

        return rst;
    }
}
