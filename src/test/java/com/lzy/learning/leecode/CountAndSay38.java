package com.lzy.learning.leecode;

public class CountAndSay38 {
    public String countAndSay(int n) {
        if (1 == n) {
            return "1";
        }

        if (2 == n) {
            return "11";
        }

        String lastStr = "11";

        for (int i = 2; i < n; i++) {
            StringBuilder sb = new StringBuilder("");
            char currentChar = lastStr.charAt(0);
            for (int start = 0, end = 1; end < lastStr.length(); ) {
                if (currentChar != lastStr.charAt(end)) {
                    sb.append(end - start).append(currentChar);
                    start = end;
                    currentChar = lastStr.charAt(start);
                }

                if (++end >= lastStr.length()) {
                    sb.append(end - start).append(currentChar);
                }
            }
            lastStr = sb.toString();
        }

        return lastStr;
    }

    public String countAndSay1(int n) {
        if (1 == n) {
            return "1";
        }

        if (2 == n) {
            return "11";
        }

        String lastStr = countAndSay(n - 1);
        StringBuilder sb = new StringBuilder("");
        char currentChar = lastStr.charAt(0);
        for (int start = 0, end = 1; end < lastStr.length(); ) {
            if (currentChar != lastStr.charAt(end)) {
                sb.append(end - start).append(currentChar);
                start = end;
                currentChar = lastStr.charAt(start);
            }

            if (++end >= lastStr.length()) {
                sb.append(end - start).append(currentChar);
            }
        }

        return sb.toString();
    }
}
