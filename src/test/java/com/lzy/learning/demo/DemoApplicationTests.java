package com.lzy.learning.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

}

class FoundNextArray {
    @Test
    void nextPermutationTest() {
        int[] nums = new int[]{1, 2, 3, 6, 8, 1, 132, 1, 3};
        nextPermutation(nums);
        Arrays.stream(nums).forEach(System.out::print);
    }

    public void nextPermutation(int[] nums) {
        if (nums.length < 2) {
            return;
        }
        boolean found = false;
        int i = 1;
        for (i = nums.length - 2; i >= 0; i--) {
            for (int j = nums.length - 1; !found && j > i; j--) {
                if (nums[j] > nums[i]) {
                    int temp = nums[j];
                    nums[j] = nums[i];
                    nums[i] = temp;
                    found = true;
                }
            }
            if (found) {
                break;
            }
        }

        revert(nums, i);
    }

    void revert(int[] nums, int minIndex) {
        for (int i = nums.length -1, j = minIndex + 1; i > j; i--, j++) {
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
    }
}
