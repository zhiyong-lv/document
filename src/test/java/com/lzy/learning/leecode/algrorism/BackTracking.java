package com.lzy.learning.leecode.algrorism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BackTracking {
}

class Solution37 {
    final List<List<Integer>> usedBitMap = new ArrayList<>();
    final List<Character> fullCharacterSet = Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9');
    final List<Set<Character>> usedRow = Arrays.asList(new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9), new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9), new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9));
    final List<Set<Character>> usedCol = Arrays.asList(new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9), new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9), new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9));
    final List<Set<Character>> usedBlock = Arrays.asList(new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9), new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9), new HashSet<>(9), new HashSet<>(9),
            new HashSet<>(9));
    int remain = 0;

    public static void main(String[] args) {
        final char[][] board = new char[][]{
                {'5', '3', '.', '.', '7', '.', '.', '.', '.'},
                {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
                {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
                {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
                {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
                {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
                {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
                {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
                {'.', '.', '.', '.', '8', '.', '.', '7', '9'}
        };
        final Solution37 solution37 = new Solution37();
        solution37.solveSudoku(board);
        System.out.print("board is:\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board[i][j] + ",");
            }
            System.out.println("");
        }
        System.out.printf("result is %s\n", board);
    }

    public void solveSudoku(char[][] board) {
        // row, col, block
        initUsed(board);
        backTracking(board, 0, 0);
    }

    void backTracking(char[][] board, int row, int col) {
        while (row < 9 && col < 9 && board[row][col] != '.') {
            row = row + ((col + 1) / 9);
            col = (col + 1) % 9;
        }

        if (remain == 0 || row >= 9 || col >= 9) return;

        final Set<Character> rowSet = usedRow.get(row);
        final Set<Character> colSet = usedCol.get(col);
        final Set<Character> blockSet = usedBlock.get((row / 3) * 3 + col / 3);
        final Set<Character> pointSet = Stream.of(rowSet, colSet, blockSet)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        for (char ch : fullCharacterSet) {
            if (pointSet.contains(ch)) continue;
            board[row][col] = ch;
            rowSet.add(ch);
            colSet.add(ch);
            blockSet.add(ch);
            remain--;
            backTracking(board, row + ((col + 1) / 9), (col + 1) % 9);
            if (remain == 0) return;
            remain++;
            rowSet.remove(ch);
            colSet.remove(ch);
            blockSet.remove(ch);
            board[row][col] = '.';
        }
    }

    void initUsed(final char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final char c = board[row][col];
                if ('.' != c) {
                    usedRow.get(row).add(c);
                    usedCol.get(col).add(c);
                    usedBlock.get((row / 3) * 3 + col / 3).add(c);
                } else {
                    remain++;
                }
            }
        }
    }
}

class Solution51 {
    public static void main(String[] args) {
        final List<List<String>> lists = new Solution51().solveNQueens(4);
        System.out.printf("result is %s\n", lists.toString());
    }

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        backTracking(result, board, n);
        return result;
    }

    private void backTracking(List<List<String>> result, char[][] board, int remainQueue) {
        if (remainQueue == 0) {
            result.add(convert(board));
            return;
        }
        int row = board.length - remainQueue;
        for (int col = 0; col < board[row].length; col++) {
            if (board[row][col] == (char) 0) {
                update(board, row, col, 1, (char) -1);
                backTracking(result, board, remainQueue - 1);
                update(board, row, col, -1, (char) 0);
            }
        }
    }

    List<String> convert(char[][] board) {
        List<String> result = new ArrayList<>();
        for (char[] chars : board) {
            char[] ca = new char[chars.length];
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == (char) -1) ca[j] = 'Q';
                else ca[j] = '.';
            }
            result.add(new String((ca)));
        }
        return result;
    }

    void update(char[][] board, int row, int col, int updateVal, char queueVal) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == row || j == col || i - row == j - col || row - i == j - col) board[i][j] += updateVal;
            }
        }
        board[row][col] = queueVal;
    }
}

class Solution332 {
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, List<String>> ticketsMap = new HashMap<>();
        tickets.forEach(l -> {
            if (ticketsMap.containsKey(l.get(0))) {
                ticketsMap.get(l.get(0)).add(l.get(1));
            } else {
                List<String> set = new ArrayList<>();
                set.add(l.get(1));
                ticketsMap.put(l.get(0), set);
            }
        });
        List<String> result = new ArrayList<>();
        backTracking(result, ticketsMap, "JFK");
        return result;
    }

    void backTracking(List<String> result, Map<String, List<String>> ticketsMap, String start) {
        if (ticketsMap.isEmpty() || !ticketsMap.containsKey(start)) {
            if (ticketsMap.isEmpty()) result.add(start);
            return;
        }

        final List<String> optionDestinations = ticketsMap.get(start);
        final List<String> destinations = optionDestinations.stream()
                .sorted(String::compareTo)
                .collect(Collectors.toList());

        result.add(start);
        for (String dest : destinations) {
            optionDestinations.remove(dest);
            if (optionDestinations.isEmpty()) ticketsMap.remove(start);
            backTracking(result, ticketsMap, dest);
            if (ticketsMap.isEmpty()) return;
            optionDestinations.add(dest);
            if (!ticketsMap.containsKey(start)) ticketsMap.put(start, optionDestinations);
        }
        result.remove(result.size() - 1);
    }
}

class Solution491 {
    public static void main(String[] args) {
        final List<List<Integer>> lists = new Solution491().findSubsequences(new int[]{1, 2, 3, 4, 1, 1, 1, 1});
        System.out.printf("result is %s\n", lists.toString());
    }

    public List<List<Integer>> findSubsequences(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        backTracking(result, list, nums, 0);
        return result;
    }

    private void backTracking(List<List<Integer>> result, List<Integer> list, int[] nums, int idx) {
        if (list.size() >= 2 || idx >= nums.length) {
            if (list.size() >= 2 && list.get(list.size() - 1) >= list.get(list.size() - 2))
                result.add(new ArrayList<>(list));
            if (idx >= nums.length || list.get(list.size() - 1) < list.get(list.size() - 2)) return;

        }
        Set<Integer> set = new HashSet<>();
        for (int i = idx; i < nums.length; i++) {
            if (set.contains(nums[i])) continue;
            set.add(nums[i]);
            list.add(nums[i]);
            backTracking(result, list, nums, i + 1);
            list.remove(list.size() - 1);
        }
    }
}

class Solution78 {
    public static void main(String[] args) {
        final List<List<Integer>> lists = new Solution78().subsets(new int[]{1, 2, 3});
        System.out.printf("result is %s\n", lists.toString());
    }

    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        backTracking(result, list, nums, 0);
        return result;
    }

    void backTracking(List<List<Integer>> result, List<Integer> list, int[] nums, int idx) {
        result.add(new ArrayList<>(list));

        for (int i = idx; i < nums.length; i++) {
            list.add(nums[i]);
            backTracking(result, list, nums, i + 1);
            list.remove(list.size() - 1);
        }
    }
}

class Solution93 {
    public static void main(String[] args) {
        final List<String> lists = new Solution93().restoreIpAddresses("25525511135");
        System.out.printf("result is %s\n", lists.toString());
    }

    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        List<String> ip = new ArrayList<>();
        backTracking(result, ip, s, 0);
        return result;
    }

    void backTracking(List<String> result, List<String> ip, String s, int start) {
        if (invalidRemainLength(ip, s, start) || ip.size() == 4 || start >= s.length()) {
            if (start == s.length() && ip.size() == 4) result.add(String.join(".", ip));
            return;
        }

        for (int i = start + 1; i <= s.length(); i++) {
            final String ipStr = validIp(s.substring(start, i));
            if (Objects.isNull(ipStr)) break;
            ip.add(ipStr);
            backTracking(result, ip, s, i);
            ip.remove(ip.size() - 1);
        }
    }

    private boolean invalidRemainLength(List<String> ip, String s, int start) {
        int min = (4 - ip.size());
        int max = (4 - ip.size()) * 3;
        final int remainLength = s.length() - start;
        return remainLength < min || remainLength > max;
    }

    private String validIp(String s) {
        if (s.length() == 0 || s.length() > 3) return null;
        if (s.charAt(0) == '0' && s.length() > 1) return null;
        int val = Integer.parseInt(s);
        return (val >= 0 && val <= 255) ? s : null;
    }
}

class Solution40 {
    public static void main(String[] args) {
        final List<List<Integer>> lists = new Solution40().combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8);
        System.out.printf("result is %s\n", lists.toString());
    }

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backTracking(result, new ArrayList<>(), candidates, 0, target, 0);
        return result;
    }

    void backTracking(List<List<Integer>> result, List<Integer> list, int[] candidates, int idx, int target, int sum) {
        if (sum >= target) {
            if (sum == target) result.add(new ArrayList<>(list));
            return;
        }

        for (int i = idx; i < candidates.length; i++) {
            if (idx != i && candidates[i] == candidates[idx]) continue;
            if (sum + candidates[i] > target) break;
            sum += candidates[i];
            list.add(candidates[i]);
            backTracking(result, list, candidates, i + 1, target, sum);
            sum -= candidates[i];
            list.remove(list.size() - 1);
        }
    }
}

class Solution39 {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backTracking(result, new ArrayList<>(), candidates, 0, target, 0);
        return result;
    }

    void backTracking(List<List<Integer>> result, List<Integer> list, int[] candidates, int idx, int target, int sum) {
        if (sum >= target) {
            if (sum == target) result.add(new ArrayList<>(list));
            return;
        }

        for (int i = idx; i < candidates.length; i++) {
            int candidate = candidates[i];
            sum += candidate;
            list.add(candidate);
            backTracking(result, list, candidates, i, target, sum);
            list.remove(list.size() - 1);
            sum -= candidate;
        }
    }
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
