package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class firstMissingPositiveNo41 {
    @Test
    void firstMissingPositiveTest() {
        int[] nums = new int[]{3,4,-1,1};
        int rst = firstMissingPositive(nums);
        Assertions.assertEquals(2, rst);
        nums = new int[]{1};
        rst = firstMissingPositive(nums);
        Assertions.assertEquals(2, rst);
        nums = new int[]{7,8,9,11,12};
        rst = firstMissingPositive(nums);
        Assertions.assertEquals(1, rst);
        nums = new int[]{1,2,0};
        rst = firstMissingPositive(nums);
        Assertions.assertEquals(3, rst);
    }

    public int firstMissingPositive(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            hashSlot(nums, i, 0);
        }

        int i = 0;
        for (; i < nums.length; i++) {
            if (nums[i] <= 0) {
                break;
            }
        }
        return i + 1;
    }

    private void hashSlot(int[] nums, int index, int replaceNum) {
        if (index >= 0 && (index / nums.length) == 0) {
            int nextNum = nums[index];
            int nextIndex = nextNum - 1;
            nums[index] = replaceNum;
            if (nextNum != replaceNum) {
                hashSlot(nums, nextIndex, nextNum);
            }
        }
    }
}
