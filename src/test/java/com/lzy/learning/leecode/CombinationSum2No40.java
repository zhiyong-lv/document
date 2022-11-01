package com.lzy.learning.leecode;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CombinationSum2No40 {
    @Test
    void sortAndMergeCandidatesTest() {
        int[] candidates = new int[]{2, 5, 2, 1, 2, 3, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 5, 3, 3, 2, 10};
        System.out.println(sortAndMergeValidCandidates(candidates, 8));
    }

    @Test
    void combinationSum2Test() {
        int[] candidates = new int[]{2,5,2,1,2};
        System.out.println(combinationSum2(candidates, 5));
    }

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> lists = new LinkedList<>();
        List<Integer> candidateMergedList = sortAndMergeValidCandidates(candidates, target);
        combinationSum2(candidateMergedList, target, 0, lists, new LinkedList<>());
        return lists;
    }

    private List<Integer> sortAndMergeValidCandidates(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<Integer> mergedCandidates = new LinkedList<>();
        for (int i = 0; i < candidates.length; i++) {
            int candidate = candidates[i];
            if (candidate > target) {
                break;
            }
            mergedCandidates.add(candidate);

            for (int duplicatedCount = 1; candidate != 0 && i + 1 < candidates.length && candidate == candidates[i + 1]; i++) {
                if ((++duplicatedCount) <= (target / candidate)) {
                    mergedCandidates.add(candidate);
                }
            }
        }
        return mergedCandidates;
    }

    private void combinationSum2(List<Integer> candidates, int target, int from, List<List<Integer>> lists, List<Integer> preList) {
        for (int i = from; i < candidates.size(); i++) {
            int candidate = candidates.get(i);
            if (i > from && candidates.get(i - 1) == candidate) {
                continue;
            }

            if (candidate > target) {
                continue;
            }

            preList.add(candidate);
            if (candidate == target) {
                lists.add(preList);
                break;
            }

            combinationSum2(candidates, target - candidate, i + 1, lists, new LinkedList<>(preList));
            preList.remove(preList.size() - 1);
        }
    }

    public int getNodeValue(int[] candidates, int target, int from, List<List<Integer>> lists, List<Integer> preList) {
        if (Objects.isNull(preList)) {
            preList = new LinkedList<>();
        }

        for (; from <= candidates.length; from++) {
            int candidate = candidates[from];
            preList.add(candidate);
            if (target == candidate) {
                lists.add(preList);
                System.out.printf("target is %d, new added list is %s\n", target, preList);
                return -3;
            }
        }

        int candidate = candidates[from];
        System.out.printf("target is %d, preList is %s, candidate is %d\n", target, preList, candidate);


        if (target > candidate) {
            if (from == 0) {
                // the target is still larger than the last items of candidate list.
                System.out.printf("target is %d, preList is %s, candidate is %d, have reached end\n", target, preList, candidate);
                return -2;
            }

            // not the last items, looking forward the
            int nextTarget = target - candidate;
            int nextFrom = from - 1;

            // try all items smaller than nextTarget, except the sum of all the rest items is still smaller than nextTarget
            for (; nextFrom >= 0; nextFrom--) {
                if (candidates[nextFrom] > nextTarget) {
                    continue;
                }
                // the target is still larger than the last items of candidate list.
                System.out.printf("next target is %d, preList is %s, candidate is %d, next from is %d\n", nextTarget, preList, candidate, nextFrom);
                if (getNodeValue(candidates, nextTarget, nextFrom, lists, new LinkedList<>(preList)) < -2) {
                    break;
                }
            }
        }

        // return when all candidate have been scanned.
        System.out.printf("target is %d, preList is %s, candidate is %d, have finished scan\n", target, preList, candidate);
        return from - 1;
    }


    public List<List<Integer>> combinationSum2Origin(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> lists = new LinkedList<>();
        for (int i = 0; i < candidates.length; i++) {
            if ((target / candidates[i]) < candidates.length - i) {
                int var = combinationSum2(candidates, target, i, lists, null);
                if (var <= 0) {
                    break;
                }

                if (var == 1) {
                    while ((i + 1) < candidates.length && candidates[i + 1] == candidates[i]) {
                        i++;
                    }
                }
            }
        }
//        Map<String, List<Integer>> dedupMap = new HashMap<>();
//        lists.forEach(l -> {
//            StringBuilder sb = new StringBuilder();
//            l.forEach(sb::append);
//            dedupMap.put(sb.toString(), l);
//        });
//        return new ArrayList<>(dedupMap.values());
        return lists;
    }

    private int combinationSum2(int[] sortedCandidates, int target, int from, List<List<Integer>> lists, List<Integer> candidatesPre) {
        int candidate = sortedCandidates[from];
        if (target < candidate) {
            // is endpoint
            return -1;
        }

        final List<Integer> candidates = Objects.isNull(candidatesPre) ? new LinkedList<>() : new LinkedList<>(candidatesPre);
        int candidateIndex = candidates.size();
        System.out.printf("target is %d, from is %d, %d at index(candidateIndex) is %d \n", target, from, candidate, candidateIndex);
        candidates.add(candidateIndex, candidate);

        if (target == candidate) {
            System.out.printf("target is %d, from is %d, candidatesPre is %s \n", target, from, candidatesPre);
            lists.add(candidates);
            // is endpoint
            return 0;
        }

        for (int i = from + 1; i < sortedCandidates.length; i++) {
            int value = combinationSum2(sortedCandidates, target - candidate, i, lists, candidates);
            if (value == 0) {
                return 1;
            }
            if (value < 0) {
                return 2;
            }

            if (value == 1) {
                while ((i + 1) < sortedCandidates.length && sortedCandidates[i + 1] == sortedCandidates[i]) {
                    i++;
                }
                return (i + 1) >= sortedCandidates.length ? 1 : 2;
            }
        }
        return 2;
    }
}
