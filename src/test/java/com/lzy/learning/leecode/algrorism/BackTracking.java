package com.lzy.learning.leecode.algrorism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackTracking {
}

class Solution216 {
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        backTracking(result, new ArrayList<>(), 0, n, k);
        return result;
    }

    void backTracking(List<List<Integer>> result, List<Integer> list, int sum, int n, int k) {
        if (k == list.size()) {
            if (sum == n) result.add(new ArrayList<>(list));
            return;
        }

        for (int i = list.isEmpty() ? 1 : list.get(list.size() - 1) + 1; i <= 9; i++) {
            sum += i;
            if (sum > n) break;
            list.add(i);
            backTracking(result, list, sum, n, k);
            list.remove(list.size() - 1);
            sum -= i;
        }
    }

}

class Solution17 {
    Map<Character, String> ch2Letter = new HashMap<>();

    {
        ch2Letter.put('2', "abc");
        ch2Letter.put('3', "def");
        ch2Letter.put('4', "ghi");
        ch2Letter.put('5', "ijk");
        ch2Letter.put('6', "mno");
        ch2Letter.put('7', "pqrs");
        ch2Letter.put('8', "tuv");
        ch2Letter.put('9', "wxyz");
    }

    public void backTracking(List<String> result, String digits, char[] chars, int idx) {
        if (idx == digits.length() && digits.length() > 0) {
            result.add(new String(chars));
            return;
        }

        final String candidate = ch2Letter.get(digits.charAt(idx));
        for (int i = 0; i < candidate.length(); i++) {
            chars[idx] = candidate.charAt(i);
            backTracking(result, digits, chars, idx + 1);
        }
    }

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        backTracking(result, digits, new char[digits.length()], 0);
        return result;
    }
}

class Solution77 {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backTracking(result, new ArrayList<>(), n, k);
        return result;
    }

    void backTracking(List<List<Integer>> result, List<Integer> list, int n, int k) {
        if (list.size() == k) {
            result.add(new ArrayList<>(list));
            return;
        }

        for (int i = list.isEmpty() ? 1 : list.get(list.size() - 1) + 1; i <= n; i++) {
            if (list.size() + n - i + 1 < k) break;
            list.add(i);
            backTracking(result, list, n, k);
            list.remove(list.size() - 1);
        }
    }
}
