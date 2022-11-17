package com.lzy.learning.leecode;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolveNQueensNo51 {
    Solution51SpeedUp solution = new Solution51SpeedUp();

    @Test
    void solveNQueensTest() {
        final List<List<String>> x = solution.solveNQueens(4);
        System.out.printf("size is %d, value is %s\n", x.size(), x);
    }
}

class Solution51SpeedUp {
    private int bitMask;

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<List<String>>();
        bitMask = (1 << n) - 1;
        int[] candidates = new int[n];
        for(int i = 0; i < candidates.length; i++) {
            candidates[i] = -1;
        }
        solveNQueens(n, 0, candidates, result);
        return result;
    }

    private void solveNQueens(int n, int currentRow, int[] candidates, List<List<String>> result) {
        if (currentRow == n) {
            result.add(convert(candidates));
            return;
        }

        final int nextCandidateCols = getCandidateCols(n, currentRow, candidates);
        if ((nextCandidateCols & bitMask) == 0) {
            return;
        }

        for (int nextCandidateColIdx = 0; nextCandidateColIdx < n; nextCandidateColIdx++) {
            if (((1 << nextCandidateColIdx) & nextCandidateCols) != 0) {
                candidates[currentRow] = nextCandidateColIdx;
                solveNQueens(n, currentRow + 1, candidates, result);
                candidates[currentRow] = -1;
            }
        }
    }

    private List<String> convert(int[] candidates) {
        final int size = candidates.length;
        List<String> rst = new ArrayList<>(size);
        for (int col : candidates) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append((i == col) ? "Q" : ".");
            }
            rst.add(sb.toString());
        }
        return rst;
    }

    private int getCandidateCols(int n, int currentRow, int[] candidates) {
        // final List<Integer> cols = IntStream.range(0, n).boxed().collect(Collectors.toList());
        int bitMap = bitMask;
        if (currentRow == 0) {
            return bitMap;
        }

        for (int candidateRow = 0; candidateRow < n - currentRow; candidateRow++) {
            final Integer candidateCol = candidates[candidateRow];
            bitMap &= ~(1 << candidateCol);
            int smallIndex = candidateCol - currentRow + candidateRow;
            if (smallIndex >= 0) {
                bitMap &= ~(1 << smallIndex);
            }
            int bigIndex = candidateCol + currentRow - candidateRow;
            if (bigIndex < n) {
                bitMap &= ~(1 << bigIndex);
            }
        }
        return bitMap;
    }

    private void solveNQueens(int n, int currentRow, List<Integer> candidates, List<List<String>> result) {
        if (currentRow == n) {
            result.add(convert(candidates));
            return;
        }

        final List<Integer> nextCandidateCols = getCandidateCols(n, currentRow, candidates);
        if (nextCandidateCols.size() == 0) {
            return;
        }

        for (int nextCandidateColIdx = 0; nextCandidateColIdx < nextCandidateCols.size(); nextCandidateColIdx++) {
            int nextCandidateCol = nextCandidateCols.get(nextCandidateColIdx);
            candidates.add(nextCandidateCol);
            solveNQueens(n, currentRow + 1, candidates, result);
            candidates.remove(candidates.size() - 1);
        }
    }

    private List<String> convert(List<Integer> candidates) {
        final int size = candidates.size();
        List<String> rst = new ArrayList<>(size);
        for (int col : candidates) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append((i == col) ? "Q" : ".");
            }
            rst.add(sb.toString());
        }
        return rst;
    }

    private List<Integer> getCandidateCols(int n, int currentRow, List<Integer> candidates) {
        final List<Integer> cols = IntStream.range(0, n).boxed().collect(Collectors.toList());
        if (currentRow == 0) {
            return cols;
        }

        for (int candidateRow = 0; candidateRow < candidates.size(); candidateRow++) {
            final Integer candidateCol = candidates.get(candidateRow);
            cols.remove(candidateCol);
            int smallIndex = candidateCol - currentRow + candidateRow;
            if (smallIndex >= 0) {
                cols.remove(Integer.valueOf(smallIndex));
            }
            int bigIndex = candidateCol + currentRow - candidateRow;
            if (bigIndex < n) {
                cols.remove(Integer.valueOf(bigIndex));
            }
        }
        return cols;
    }
}

class Solution51 {
    private Set<String> rst = new HashSet<>();

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<List<String>>();
        int[][] preRst = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                preRst[i][j] = 0;
            }
        }
        for (int i = 0; i < n; i++) {
            // for (int i = 1; i < 2; i++) {
            solveNQueens(n, 0, i, preRst, result);
        }
        return result;
    }

    private void solveNQueens(int restQueenCnt, int row, int col, int[][] preRst, List<List<String>> result) {
        System.out.printf("[%d,%d] - %d\n", row, col, restQueenCnt);
        if (restQueenCnt == 1) {
            preRst[row][col] = 1;
            final List<String> solution = convert(preRst);
            // if (!rst.contains(solution.toString())) {
            result.add(solution);
            rst.add(solution.toString());
            // }
            preRst[row][col] = 0;
            return;
        }
        markBoard(row, col, preRst, false);
        final List<List<Integer>> candidates = getCandidates(preRst);
        System.out.printf("[%d,%d] - %s\n", row, col, candidates);
        Arrays.stream(preRst).forEach(a -> System.out.println(Arrays.toString(a)));
        if (candidates.size() >= restQueenCnt - 1) {
            for (int candidatesIndex = 0; candidatesIndex < candidates.size(); candidatesIndex++) {
                List<Integer> position = candidates.get(candidatesIndex);
                int candidateRow = position.get(0);
                int candidateCol = position.get(1);
                if (candidateRow * preRst.length + candidateCol < row * preRst.length + col) {
                    continue;
                }
                if (candidates.size() - candidatesIndex < restQueenCnt - 1) {
                    break;
                }
                solveNQueens(restQueenCnt - 1, candidateRow, candidateCol, preRst, result);
            }
        }
        markBoard(row, col, preRst, true);
    }

    List<List<Integer>> getCandidates(int[][] map) {
        List<List<Integer>> result = new ArrayList<>();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map.length; col++) {
                if (map[row][col] == 0) {
                    result.add(Arrays.asList(row, col));
                }
            }
        }
        return result;
    }

    void markBoard(int row, int col, int[][] map, boolean toValid) {
        int invalidVal = toValid ? -2 : 2;
        int usedVal = toValid ? 0 : 1;
        for (int i = 0; i < map.length; i++) {
            map[i][col] += invalidVal;
            map[row][i] += invalidVal;
        }
        for (int i = 1; row - i >= 0; i++) {
            if (col + i < map.length) map[row - i][col + i] += invalidVal;
            if (col - i >= 0) map[row - i][col - i] += invalidVal;
        }
        for (int i = 1; row + i < map.length; i++) {
            if (col + i < map.length) map[row + i][col + i] += invalidVal;
            if (col - i >= 0) map[row + i][col - i] += invalidVal;
        }
        map[row][col] = usedVal;
    }

    List<String> convert(int[][] map) {
        final int n = map.length;
        List<String> rst = new ArrayList<>(n);
        for (int row = 0; row < n; row++) {
            int[] ints = map[row];
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < n; col++) {
                if (ints[col] == 1) {
                    sb.append("Q");
                } else {
                    sb.append(".");
                }
            }
            rst.add(sb.toString());
        }
        return rst;
    }
}
