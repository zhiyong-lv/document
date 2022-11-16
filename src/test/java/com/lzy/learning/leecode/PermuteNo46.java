package com.lzy.learning.leecode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PermuteNo46 {
    @ParameterizedTest
    @CsvSource({
            "'1,2,3','[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]'"
    })
    public void permute(String intArrStr, String expected) {
        int[] nums = Arrays.stream(intArrStr.split(",")).mapToInt(Integer::valueOf).toArray();
        System.out.println(new Solution46().permute(nums));
    }
}

class Solution46 {
    public List<List<Integer>> permute(int[] nums) {
        Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        for (int i = 0; i < nums.length; i++) {
            used[i] = false;
        }
        List<List<Integer>> result = new ArrayList<>();
        permute(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void permute(int[] nums, boolean[] used, List<Integer> preRst, List<List<Integer>> result) {
        if (preRst.size() == nums.length) {
            result.add(new ArrayList<>(preRst));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            Integer candidate = nums[i];
            used[i] = true;
            preRst.add(candidate);
            permute(nums, used, preRst, result);
            preRst.remove(candidate);
            used[i] = false;
        }
    }

    private void permute(List<Integer> candidates, List<Integer> preRst, List<List<Integer>> result) {
        if (candidates.isEmpty()) {
            result.add(new ArrayList<>(preRst));
            return;
        }
        final ArrayList<Integer> toScanCandidates = new ArrayList<>(candidates);
        for (int i = 0; i < toScanCandidates.size(); i++) {
            final Integer candidate = toScanCandidates.get(i);
            candidates.remove(candidate);
            preRst.add(candidate);
            permute(candidates, preRst, result);
            preRst.remove(candidate);
            candidates.add(candidate);
        }
    }
}
