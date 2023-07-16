package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

public class LeeCodeFrom66 {
    @ParameterizedTest
    @CsvSource({
//            "'This,is,an,example,of,text,justification.',16,3",
            //["Science  is  what we","understand      well","enough to explain to","a  computer.  Art is","everything  else  we","do                  "]
            "'enough,to,explain,to,a,computer.,Art,is,everything,else,we,do',20,4",
            "'for,your,country',16,1"
    })
    void greedyFeedTest(String words, int maxWidth, int expectedListSize) {
        List<String> rst = new ArrayList<>();
        new Solution68().greedyFeed(words.split(","), maxWidth, rst, 0, 0, 0);
        Assertions.assertEquals(expectedListSize, rst.size());
    }

    @ParameterizedTest
    @CsvSource({
            "4,2",
            "0,0",
            "1,1",
            "2,1",
            "3,1",
            "5,2",
            "8,2",
            "9,3",
    })
    void mySqrtTest(int input, int expect) {
        Assertions.assertEquals(expect, new Solution69().mySqrt(input));
    }
}

class Solution70 {
    public int climbStairs(int n) {
        int[] rst = new int[n];
        rst[0] = 1;
        rst[1] = 2;
        for (int i = 2; i < n; i++) {
            rst[i] = rst[i - 1] + rst[i - 2];
        }
        return rst[n - 1];
    }
}

class Solution69 {
    public int mySqrt(int x) {
        if (x <= 1) {
            return x;
        }

        int div = 1, rest = x;
        while (div < rest) {
            div *= 2;
            rest /= 2;
        }
        int s = rest, l = div;
        while (s < l) {
            int mid = (s + l) / 2;
            if (mid == s) {
                return mid;
            }

            int multiRst = mid * mid;
            if (multiRst > x || multiRst < 0) {
                l = mid;
            } else if (multiRst < x) {
                s = mid;
            } else {
                return mid;
            }
        }

        return s;
    }
}

class Solution68 {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> rst = new ArrayList<>();
        greedyFeed(words, maxWidth, rst, 0, 0, 0);
        return rst;
    }

    void greedyFeed(String[] words, int maxWidth, List<String> rst, int startWordIdx, int wordIdx, int length) {
        if (wordIdx >= words.length) {
            if (wordIdx > startWordIdx) {
                rst.add(formatLine(words, startWordIdx, wordIdx, maxWidth, length));
            }
            return;
        }

        String word = words[wordIdx];
        int wordLength = word.length();
        int preLength = length;
        if (length == 0) {
            length = wordLength;
        } else {
            length = length + wordLength + 1;
        }

        int nextWordIdx = length > maxWidth ? wordIdx : wordIdx + 1;
        if (length >= maxWidth) {
            rst.add(formatLine(words, startWordIdx, nextWordIdx, maxWidth, length > maxWidth ? preLength : length));
            startWordIdx = nextWordIdx;
            length = 0;
        }

        greedyFeed(words, maxWidth, rst, startWordIdx, nextWordIdx, length);
    }

    String formatLine(String[] words, int startWordIdx, int endIdx, int maxWidth, int length) {
        StringBuilder sb = new StringBuilder();
        int wordCount = endIdx - startWordIdx;
        if (endIdx >= words.length) {
            for (int i = startWordIdx; i < words.length; i++) {
                sb.append(words[i]).append(" ");
            }
            if (sb.length() > maxWidth) {
                return sb.toString().trim();
            }
            while (sb.length() < maxWidth) {
                sb.append(" ");
            }
            return sb.toString();
        } else if (wordCount == 1) {
            sb.append(words[startWordIdx]);
            while (sb.length() < maxWidth) {
                sb.append(" ");
            }
            return sb.toString();
        } else {
            int spaceCount = maxWidth - length + wordCount - 1;
            int[] spaceCounts = new int[wordCount - 1];
            for (int i = 0; i < spaceCount; i++) {
                int idx = i % spaceCounts.length;
                spaceCounts[idx] += 1;
            }
            for (int i = startWordIdx, j = 0; i < endIdx; i++, j++) {
                sb.append(words[i]);
                for (int spaceCnt = 0; j < spaceCounts.length && spaceCnt < spaceCounts[j]; spaceCnt++) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }
    }
}

class Solution66 {
    public int[] plusOne(int[] digits) {
        int nextPlus = 0;
        for (int i = digits.length - 1; i >= 0; i--) {
            int sum = digits[i] + 1;
            nextPlus = sum / 10;
            digits[i] = sum % 10;

            if (nextPlus == 0) break;
        }

        if (nextPlus > 0) {
            int[] newDigits = new int[digits.length + 1];
            newDigits[0] = nextPlus;
            for (int i = 0; i < digits.length - 1; i++) {
                newDigits[i + 1] = digits[i];
            }
            return newDigits;
        }

        return digits;
    }
}

class Solution67 {
    public String addBinary(String a, String b) {
        int aIdx = a.length() - 1;
        int bIdx = b.length() - 1;

        StringBuilder sb = new StringBuilder();
        int nextPlus = 0;
        while (aIdx >= 0 || bIdx >= 0 || nextPlus > 0) {
            int cha = (aIdx >= 0) ? a.charAt(aIdx) - '0' : 0;
            int chb = (bIdx >= 0) ? b.charAt(bIdx) - '0' : 0;
            int sum = cha + chb + nextPlus;
            nextPlus = sum / 2;
            sb.append(sum % 2);
            aIdx--;
            bIdx--;
        }
        return sb.reverse().toString();
    }
}
