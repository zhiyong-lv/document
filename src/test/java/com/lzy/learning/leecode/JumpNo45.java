package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JumpNo45 {
    @ParameterizedTest
    @CsvSource({
            "'2,3,1,1,4',2",
            "'2,3,0,1,4',2",
            "'2,0,2,4,6,0,0,3',3",
            "'5,9,3,2,1,0,2,3,3,1,0,0',3",
            "'0',0",
            "'1',0",
    })
    void testJump(String intArrStr, int expectedRst) {
        int[] nums = Arrays.stream(intArrStr.split(",")).mapToInt(Integer::valueOf).toArray();
        Assertions.assertEquals(expectedRst, new Solution().jump(nums));
    }
}

class Solution {
    public int jump(int[] nums) {
        int[] dp = new int[nums.length];
        for(int i = 0; i < dp.length; i++) {
            dp[i] = -1;
        }
        return jump(nums, 0, dp);
    }

    public int jump(int[] nums, int start, int[] dp) {
        int val = nums[start], length = nums.length;

        if (start == length - 1) {
            return 0;
        }

        if (val >= (length - start - 1)) {
            return 1;
        }

        if (dp[start] != -1) {
            return dp[start];
        }

        int step = 0;
        for (int i = val; i > 0; i--) {
            if (nums[start + i] == 0) {
                continue;
            }

            int newStep = jump(nums, start + i, dp);
            if (newStep == 1) {
                step = newStep + 1;
                break;
            }
            if (newStep > 0 && (newStep < step || step == 0)) {
                step = newStep + 1;
            }
        }

        dp[start] = step;
        return step;
    }
}
