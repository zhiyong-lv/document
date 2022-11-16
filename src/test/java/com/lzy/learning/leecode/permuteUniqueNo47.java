package com.lzy.learning.leecode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class permuteUniqueNo47 {
    @ParameterizedTest
    @CsvSource({
            // "'1,1,2','[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]'",
            "'2,2,1,1','[[1,1,2,2],[1,2,1,2],[1,2,2,1],[2,1,1,2],[2,1,2,1],[2,2,1,1]]'",
    })
    public void permute(String intArrStr, String expected) {
        int[] nums = Arrays.stream(intArrStr.split(",")).mapToInt(Integer::valueOf).toArray();
        System.out.println(new Solution47().permuteUnique(nums));
    }
}

class Solution47 {
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        for (int i = 0; i < nums.length; i++) {
            used[i] = false;
        }
        List<Integer> preList = new ArrayList<>(nums.length);
        List<List<Integer>> result = new ArrayList<>();
        permuteUnique(nums, used, preList, result);
        return result;
    }

    private void permuteUnique(int[] nums, boolean[] used, List<Integer> preList, List<List<Integer>> result) {
        if (isAllUsed(used)) {
            result.add(new ArrayList<>(preList));
            return;
        }

        Set<Integer> candidates = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            final int candidate = nums[i];
            if (!candidates.contains(candidate)) {
                candidates.add(candidate);
                used[i] = true;
                preList.add(candidate);
                permuteUnique(nums, used, preList, result);
                preList.remove(preList.size() - 1);
                used[i] = false;
            }
        }
    }

    private boolean isAllUsed(boolean[] used) {
        boolean allUsed = true;
        for (boolean b : used) {
            if (!b) {
                allUsed = false;
                break;
            }
        }
        return allUsed;
    }
}
