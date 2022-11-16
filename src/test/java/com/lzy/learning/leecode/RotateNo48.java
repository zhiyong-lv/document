package com.lzy.learning.leecode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

public class RotateNo48 {
    @ParameterizedTest
    @CsvSource(
            "'[[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]'"
    )
    void rotateTest(String arrStr) {
        final String[] firstLevelFields = arrStr.split("\\],");
        int[][] arr = new int[firstLevelFields.length][firstLevelFields.length];
        for (int row = 0; row < firstLevelFields.length; row++) {
            String firstLevelField = firstLevelFields[row];
            final String[] nums = firstLevelField.replaceAll("[\\]\\[]", "").split(",");
            for(int col=0; col<nums.length; col++) {
                arr[row][col] = Integer.parseInt(nums[col]);
            }
        }
        for(int[] subArr : arr) {
            System.out.println(Arrays.toString(subArr));
        }
        new Solution48().rotate(arr);

        for(int[] subArr : arr) {
            System.out.println(Arrays.toString(subArr));
        }
    }
}

class Solution48 {
    public void rotate(int[][] matrix) {
        final int length = matrix.length;
        for (int i = 0; i < length / 2 + 1; i++) {
            for (int j = i; j < length - i - 1; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[length - j - 1][i];
                matrix[length - j - 1][i] = matrix[length - i - 1][length - j - 1];
                matrix[length - i - 1][length - j - 1] = matrix[j][length - i - 1];
                matrix[j][length - i - 1] = temp;
            }
        }
    }
}
