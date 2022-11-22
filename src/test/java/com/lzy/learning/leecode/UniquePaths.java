package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UniquePaths {
    @ParameterizedTest
    @CsvSource({
            "3,7,28",
            "3,2,3",
            "7,3,28",
            "3,3,6",
            "1,1,1",
    })
    void uniquePathsTest(int m, int n, int expectedRst) {
        Assertions.assertEquals(expectedRst, new Solution62().uniquePaths(m, n));
    }

    /**
     * 部分有效数字列举如下：["2", "0089", "-0.1", "+3.14", "4.", "-.9", "2e10", "-90E3", "3e+7", "+6e-1", "53.5e93", "-123.456e789"]
     * <p>
     * 部分无效数字列举如下：["abc", "1a", "1e", "e3", "99e2.5", "--6", "-+3", "95a54e53"]
     *
     * @param s
     * @param expectedRst
     */
    @ParameterizedTest
    @CsvSource({
            "2,true",
            "0089,true",
            "-0.1,true",
            "+3.14,true",
            "4.,true",
            "-.9,true",
            "46.e3,true",
            "2e10,true",
            "-90E3,true",
            "3e+7,true",
            "0,true",
    })
    void isNumberTest(String s, boolean expectedRst) {
        Assertions.assertEquals(expectedRst, new Solution65().isNumber(s));
    }
}

class Solution65V2 {

    public boolean isNumber(String s) {
        int[][] stateTransferTable = new int[][]{
                new int[]{1, 2, 4, -1, -1, -1}, // state 0
                new int[]{-1, 2, 3, -1, -1, -1}, // state 1
                new int[]{-1, 2, 3, 6, 0, -1}, // state 2
                new int[]{-1, 5, -1, 6, 0, -1}, // state 3
                new int[]{-1, 5, -1, -1, -1, -1}, // state 4
                new int[]{-1, 5, -1, 6, 0, -1}, // state 5
                new int[]{7, 8, -1, -1, -1, -1}, // state 6
                new int[]{-1, 8, -1, -1, -1, -1}, // state 7
                new int[]{-1, 8, -1, -1, 0, -1}, // state 8
        };
        int i = 0, state = 0;
        while (true) {
            if (i == s.length()) {
                state = stateTransferTable[state][4];
            } else {
                switch (s.charAt(i)) {
                    case '+':
                    case '-':
                        state = stateTransferTable[state][0];
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        state = stateTransferTable[state][1];
                        break;
                    case '.':
                        state = stateTransferTable[state][2];
                        break;
                    case 'e':
                    case 'E':
                        state = stateTransferTable[state][3];
                        break;
                    default:
                        state = stateTransferTable[state][5];
                }
            }
            if (state == 0) {
                return true;
            }

            if (state == -1) {
                return false;
            }

            i++;
        }
    }
}

class Solution65 {
    public boolean isNumber(String s) {
        int[] signRst = isSign(s, 0);
        if (signRst[0] < 0) {
            return false;
        }
        int startIdx = signRst[1];
        int[] integerRst = isInteger(s, startIdx);
        if (integerRst[0] < 0) {
            return false;
        }

        if (integerRst[1] > 0) {
            if (isLastIdx(s, integerRst[1])) {
                return true;
            }
            startIdx = integerRst[1];
        }

        int[] secondHalfFloat = isSecondHalfFloat(s, integerRst[1]);
        if (secondHalfFloat[0] < 0) {
            return false;
        }

        if (secondHalfFloat[0] == 0) {
            if (integerRst[0] == 0) {
                return false;
            } else {
                int[] floatSign = isFloatSign(s, secondHalfFloat[1]);
                if (floatSign[0] < 0) {
                    return false;
                }

                if (floatSign[0] > 0) {
                    if (isLastIdx(s, floatSign[1])) {
                        return true;
                    }
                    startIdx = floatSign[1];
                }
            }
        } else {
            startIdx = secondHalfFloat[1];
        }

        if (isLastIdx(s, startIdx)) {
            return true;
        }

        int[] scienceSign = isScienceSign(s, startIdx);
        if (scienceSign[0] <= 0) {
            return false;
        } else {
            startIdx = scienceSign[1];
        }

        int[] sndSignRst = isSign(s, startIdx);
        if (sndSignRst[0] < 0) {
            return false;
        }

        startIdx = sndSignRst[1];
        int[] sndIntegerRst = isInteger(s, startIdx);
        if (sndIntegerRst[0] <= 0) {
            return false;
        } else {
            startIdx = sndIntegerRst[1];
        }

        return isLastIdx(s, startIdx);
    }

    int[] isScienceSign(String s, final int startIdx) {
        char ch;
        if (startIdx >= s.length()) {
            return new int[]{-1, startIdx};
        }
        if ((ch = s.charAt(startIdx)) != 'e' && ch != 'E') {
            return new int[]{0, startIdx};
        } else {
            return new int[]{1, startIdx + 1};
        }
    }

    int[] isSign(String s, final int startIdx) {
        char ch;
        if (startIdx >= s.length()) {
            return new int[]{-1, startIdx};
        }
        if ((ch = s.charAt(startIdx)) != '-' && ch != '+') {
            return new int[]{0, startIdx};
        } else {
            return new int[]{1, startIdx + 1};
        }
    }

    int[] isFloatSign(String s, final int startIdx) {
        if (startIdx >= s.length()) {
            return new int[]{-1, startIdx};
        }
        if (s.charAt(startIdx) != '.') {
            return new int[]{0, startIdx};
        } else {
            return new int[]{1, startIdx + 1};
        }
    }

    int[] isInteger(String s, final int startIdx) {
        if (startIdx >= s.length()) {
            return new int[]{-1, startIdx};
        }

        int idx = startIdx;
        while (idx < s.length() && s.charAt(idx) >= '0' && s.charAt(idx) <= '9') {
            idx++;
        }
        return new int[]{idx - startIdx, idx};
    }

    int[] isSecondHalfFloat(String s, final int startIdx) {
        int[] floatSignRst = isFloatSign(s, startIdx);
        if (floatSignRst[0] < 0) {
            return new int[]{-1, startIdx};
        }
        if (floatSignRst[0] == 0) {
            return new int[]{0, startIdx};
        }

        int[] lastIntegerRst;
        if ((lastIntegerRst = isInteger(s, floatSignRst[1]))[0] > 0) {
            return new int[]{lastIntegerRst[1] - startIdx, lastIntegerRst[1]};
        } else {
            return new int[]{0, startIdx};
        }
    }

    boolean isLastIdx(String s, final int startIdx) {
        return startIdx >= s.length();
    }
}

class Solution64 {
    public int minPathSum(int[][] grid) {
        int maxRow = grid.length;
        int maxCol = grid[0].length;
        int[][] cachedPaths = new int[maxRow][maxCol];
        return dp(0, 0, maxRow, maxCol, grid, cachedPaths);
    }

    private int dp(int rowIdx, int colIdx, int maxRow, int maxCol, int[][] grid, int[][] cachedPaths) {
        if (cachedPaths[rowIdx][colIdx] > 0) {
            return cachedPaths[rowIdx][colIdx];
        }

        if (rowIdx == maxRow - 1 && colIdx == maxCol - 1) {
            return grid[rowIdx][colIdx];
        }

        if (rowIdx == maxRow - 1) {
            return (cachedPaths[rowIdx][colIdx] = grid[rowIdx][colIdx] + dp(rowIdx, colIdx + 1, maxRow, maxCol, grid, cachedPaths));
        }

        if (colIdx == maxCol - 1) {
            return (cachedPaths[rowIdx][colIdx] = grid[rowIdx][colIdx] + dp(rowIdx + 1, colIdx, maxRow, maxCol, grid, cachedPaths));
        }

        return (cachedPaths[rowIdx][colIdx] = grid[rowIdx][colIdx] + Math.min(dp(rowIdx + 1, colIdx, maxRow, maxCol, grid, cachedPaths),
                dp(rowIdx, colIdx + 1, maxRow, maxCol, grid, cachedPaths)));
    }
}

class Solution62 {
    public int uniquePaths(int m, int n) {
        int[][] cachedPaths = new int[m][n];
        return dp(0, 0, m, n, cachedPaths);
    }

    private int dp(int rowIdx, int colIdx, int maxRow, int maxCol, int[][] cachedPaths) {
        if (cachedPaths[rowIdx][colIdx] > 0) {
            return cachedPaths[rowIdx][colIdx];
        }

        if (rowIdx == maxRow - 1 || colIdx == maxCol - 1) {
            cachedPaths[rowIdx][colIdx] = 1;
            return 1;
        }

        cachedPaths[rowIdx][colIdx] = dp(rowIdx + 1, colIdx, maxRow, maxCol, cachedPaths) + dp(rowIdx, colIdx + 1, maxRow, maxCol, cachedPaths);
        return cachedPaths[rowIdx][colIdx];
    }
}

class Solution63 {
    private int uniquePathsWithObstacles2(int[][] obstacleGrid) {
        int maxRow = obstacleGrid.length;
        int maxCol = obstacleGrid[0].length;

        for (int row = maxRow - 1; row >= 0; row--) {
            for (int col = maxCol - 1; col >= 0; col--) {
                if (row == maxRow - 1 && col == maxCol - 1) {
                    if (obstacleGrid[row][col] == 1) {
                        return 0;
                    }

                    obstacleGrid[maxRow - 1][maxCol - 1] = 1;
                } else if (obstacleGrid[row][col] == 1) {
                    obstacleGrid[row][col] = 0;
                } else {
                    if (row < maxRow - 1) {
                        obstacleGrid[row][col] += obstacleGrid[row + 1][col];
                    }
                    if (col < maxCol - 1) {
                        obstacleGrid[row][col] += obstacleGrid[row][col + 1];
                    }
                }
            }
        }
        return obstacleGrid[0][0];
    }

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int maxRow = obstacleGrid.length;
        int maxCol = obstacleGrid[0].length;
        int[][] cachedPaths = new int[maxRow][maxCol];
        return dp(0, 0, maxRow, maxCol, obstacleGrid, cachedPaths);
    }


    private int dp(int rowIdx, int colIdx, int maxRow, int maxCol, int[][] obstacleGrid, int[][] cachedPaths) {
        if (obstacleGrid[rowIdx][colIdx] == 1) {
            return 0;
        }

        if (cachedPaths[rowIdx][colIdx] > 0) {
            return cachedPaths[rowIdx][colIdx];
        }

        if (rowIdx == maxRow - 1 && colIdx == maxCol - 1) {
            cachedPaths[rowIdx][colIdx] = obstacleGrid[rowIdx][colIdx] == 1 ? 0 : 1;
            return cachedPaths[rowIdx][colIdx];
        }

        if (rowIdx == maxRow - 1) {
            cachedPaths[rowIdx][colIdx] = dp(rowIdx, colIdx + 1, maxRow, maxCol, obstacleGrid, cachedPaths);
            return cachedPaths[rowIdx][colIdx];
        }

        if (colIdx == maxCol - 1) {
            cachedPaths[rowIdx][colIdx] = dp(rowIdx + 1, colIdx, maxRow, maxCol, obstacleGrid, cachedPaths);
            return cachedPaths[rowIdx][colIdx];
        }

        cachedPaths[rowIdx][colIdx] = dp(rowIdx + 1, colIdx, maxRow, maxCol, obstacleGrid, cachedPaths) + dp(rowIdx, colIdx + 1, maxRow, maxCol, obstacleGrid, cachedPaths);
        return cachedPaths[rowIdx][colIdx];
    }
}
