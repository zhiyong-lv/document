package com.lzy.learning.leecode;

public class MaxSubArrayNo53 {
    public void maxSubArray(int[] nums) {

    }
}

class Solution53 {
    public int maxSubArray(int[] nums) {
        int max = nums[0];
        int[] maxArr = new int[nums.length];
        maxArr[0] = max;

        for (int i = 1; i < nums.length; i++) {
            maxArr[i] = (maxArr[i - 1] + nums[i] > nums[i]) ? maxArr[i - 1] + nums[i] : nums[i];
            if (maxArr[i] > max) {
                max = maxArr[i];
            }
        }

        return max;
    }
}
