package com.lzy.learning.leecode;

import org.junit.jupiter.api.Test;

import java.util.*;

public class SolveSudoku37 {
    @Test
    void solveSudokuTest() {
        char[][] board = new char[][]{{'5', '3', '.', '.', '7', '.', '.', '.', '.'}, {'6', '.', '.', '1', '9', '5', '.', '.', '.'}, {'.', '9', '8', '.', '.', '.', '.', '6', '.'}, {'8', '.', '.', '.', '6', '.', '.', '.', '3'}, {'4', '.', '.', '8', '.', '3', '.', '.', '1'}, {'7', '.', '.', '.', '2', '.', '.', '.', '6'}, {'.', '6', '.', '.', '.', '.', '2', '8', '.'}, {'.', '.', '.', '4', '1', '9', '.', '.', '5'}, {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};
        solveSudoku(board);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                System.out.printf("%c,", board[row][col]);
            }
            System.out.println("");
        }
    }

    public void solveSudoku(char[][] board) {
        Map<Integer, List<Set<Character>>> validMap = createValidMap();
        initValidMap(board, validMap);
        solveSudoku(board, validMap, 0, 0);
    }

    private boolean solveSudoku(char[][] board, Map<Integer, List<Set<Character>>> validMap, int rowStart, int colStart) {
        int mapRowFlag = 0;
        int mapColFlag = 1;
        int mapZoneFlag = 2;
        boolean done = true;
        int row = rowStart;
        int col;
        for (; row < 9; row++) {
            for (col = colStart; col < 9; col++) {
                colStart = 0;
                char c = board[row][col];
                if (c != '.') {
                    continue;
                }

                done = false;
                Set<Character> validRowChars = validMap.get(mapRowFlag).get(row);
                Set<Character> validColChars = validMap.get(mapColFlag).get(col);
                Set<Character> validZoneChars = validMap.get(mapZoneFlag).get(getIndexInZone(row, col));

                Set<Character> candidates = new HashSet<>(validRowChars);
                candidates.retainAll(validColChars);
                candidates.retainAll(validZoneChars);

                if (candidates.isEmpty()) {
                    return false;
                }

                for (char nextCandidate : candidates) {
                    validRowChars.remove(nextCandidate);
                    validColChars.remove(nextCandidate);
                    validZoneChars.remove(nextCandidate);

                    board[row][col] = nextCandidate;

                    int nextRow = row;
                    int nextCol = (col + 1) % 9;
                    if (nextCol < col) {
                        nextRow = (row + 1) % 9;
                    }

                    if (solveSudoku(board, validMap, nextRow, nextCol)) {
                        return true;
                    }

                    board[row][col] = '.';

                    validRowChars.add(nextCandidate);
                    validColChars.add(nextCandidate);
                    validZoneChars.add(nextCandidate);
                }

                return false;
            }
        }

        return done;
    }

    private Map<Integer, List<Set<Character>>> createValidMap() {
        Map<Integer, List<Set<Character>>> validMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            List<Set<Character>> subList = new ArrayList<>(9);
            for (int j = 0; j < 9; j++) {
                Set<Character> validChars = new HashSet<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9'));
                subList.add(validChars);
                validMap.put(i, subList);
            }
        }
        return validMap;
    }

    private void initValidMap(char[][] board, Map<Integer, List<Set<Character>>> validMap) {
        int mapRowFlag = 0;
        int mapColFlag = 1;
        int mapZoneFlag = 2;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                char c = board[row][col];
                if (c == '.') {
                    continue;
                }

                validMap.get(mapRowFlag).get(row).remove(c);
                validMap.get(mapColFlag).get(col).remove(c);
                validMap.get(mapZoneFlag).get(getIndexInZone(row, col)).remove(c);
            }
        }
    }

    private int getIndexInZone(int row, int col) {
        return (row / 3) * 3 + (col / 3);
    }
}
