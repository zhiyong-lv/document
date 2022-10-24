package com.lzy.learning.leecode;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class CombinationSum39 {
    @Test
    void combinationSumTest() {
        List<List<Integer>> lists = combinationSum(new int[]{2, 3, 6, 7}, 7);
//        List<List<Integer>> lists = combinationSum(new int[]{2, 3, 5}, 8);
        System.out.println(lists);
    }

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        return combinationSumInSubArray(candidates, target, 0);
    }

    private List<List<Integer>> combinationSumInSubArray(int[] candidates, int target, int start) {
        LinkedList<List<Integer>> combinationSumList = new LinkedList<>();

        if (candidates.length == 1) {
            int candidate = candidates[start];
            int count = target / candidate;
            if (count <= 0) {
                return combinationSumList;
            }

            if (count * candidate == target) {
                LinkedList<Integer> candidateList = new LinkedList<>();
                for (int k = 0; k < count; k++) {
                    candidateList.add(candidate);
                }
                combinationSumList.add(candidateList);
            }

            return combinationSumList;
        }

        for (int i = start; i < candidates.length; i++) {
            int candidate = candidates[i];
            int count = target / candidate;
            if (count == 0) {
                continue;
            }

            for (int j = Math.abs(count); j > 0; j--) {
                if (j * candidate == target) {
                    LinkedList<Integer> candidateList = new LinkedList<>();
                    for (int k = 0; k < j; k++) {
                        candidateList.add(candidate);
                    }
                    combinationSumList.add(candidateList);
                }

                List<List<Integer>> subLists;
                if (j == count) {
                    subLists = combinationSumInSubArray(candidates, target - count * candidate, i + 1);
                } else {
                    subLists = combinationSumInSubArray(candidates, target - j * candidate, i + 1);
                }

                if (subLists.size() > 0) {
                    for (List<Integer> sublist : subLists) {
                        for (int k = 0; k < j; k++) {
                            ((LinkedList<Integer>) sublist).addFirst(candidate);
                        }
                        combinationSumList.add(sublist);
                    }
                }
            }
        }

        return combinationSumList;
    }
}
