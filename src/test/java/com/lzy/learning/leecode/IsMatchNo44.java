package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashSet;
import java.util.Set;

public class IsMatchNo44 {
    @ParameterizedTest
    @CsvSource({
            "'',''",
            "'*','*'",
            "'***********','*'",
            "'*1*','*1*'",
            "'*1*******','*1*'",
            "'*1*******2','*1*2'",
            "'123','123'",
    })
    void mergeDuplicatedStarInPatternTest(String input, String expected) {
        final Solution solution = new Solution();
        Assertions.assertEquals(expected, solution.mergeDuplicatedStarInPattern(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'','',true",
            "'hi','*?',true",
            "'baa','*a*aa',false",
            "'aa','aa',true",
            "'pi','*?i*pi',false",
            "'mississippi','m??*ss*?i*pi',false",
            "'','*',true",
            "'','***********',true",
            "'1234123412341234','***********',true",
            "'1234123412341234','******5****',false",
            "'aa','*',true",
            "'aa','a*a',true",
            "'ab','a*a',false",
            "'ab','a*b',true",
            "'abbbbb','a*b',true",
            "'aaaaab','a*b',true",
            "'accccb','a*b',true",
            "'accccba','a*b',false",
            "'cb','?a',false",
            "'adceb','*a*b',true",
            "'acdcb','a*c?b',false",
            "'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa','*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa*',true",
            "'bbabaabaabbbaabababaaabbbbaabbaababbaababababaaaabaabababbbabbbababbbbbabaaababbbbaabbbabababaaaabbbaaaaaaabbbbabbbaaabbaabbabbabaabaabaaaabbbaaabbabaababaabbaababbabbbbbaaaabababaabbaababaabbbbabaaab','b*babaabab**a*a*a*a*b*a**b*b*aaa*baa*bb**bb*ab*aa**baa**b*aa*aab*b*a***aa**abbbbaa*aabab*ab*b****b*bbbb',false",
    })
    void isMatchTest(String input, String p, boolean expected) {
        final Solution solution = new Solution();
        Assertions.assertEquals(expected, solution.isMatch(input, p));
    }

    class Solution {
        public boolean isMatch(String s, String p) {
            p = mergeDuplicatedStarInPattern(p);
            return isMatch(s, p, new HashSet<>());
        }

        private boolean isMatch(String s, String p, Set<String> cached) {
            boolean match = false;
            final String key = s + ":" + p;

            if (cached.contains(key)) {
                return match;
            }
            out:
            while (true) {
                if (p.replace("*", "").length() > s.length()) {
                    match = false;
                    break;
                }

                int reverseIndex = 0;
                for (; reverseIndex < s.length() && reverseIndex < p.length(); reverseIndex++) {
                    final char c = p.charAt(p.length() - reverseIndex - 1);
                    if (c == '*') {
                        break;
                    }
                    if (c == '?') {
                        continue;
                    }
                    if (c != s.charAt(s.length() - reverseIndex - 1)) {
                        match = false;
                        break out;
                    }
                }

                final String pSubstring = p.substring(0, p.length() - reverseIndex);
                final String sSubString = s.substring(0, s.length() - reverseIndex);
                if (pSubstring.equals("*") || (pSubstring.equals("?") && sSubString.length() == 1) || (pSubstring.length() == 0 && sSubString.length() == 0)) {
                    match = true;
                    break;
                }

                if (pSubstring.length() == 0 || sSubString.length() == 0 || (!pSubstring.contains("*") && pSubstring.length() != sSubString.length())) {
                    match = false;
                    break;
                }

                if (pSubstring.length() == 1 && sSubString.length() == 1) {
                    match = pSubstring.charAt(0) == sSubString.charAt(0);
                    break;
                }

                if (p.charAt(0) != '*') {
                    int i = 0;
                    for (; i < sSubString.length() && i < pSubstring.length(); i++) {
                        final char c = pSubstring.charAt(i);
                        if (c == '*') {
                            match = isMatch(sSubString.substring(i), pSubstring.substring(i), cached);
                            break out;
                        }
                        if (c == '?') {
                            continue;
                        }
                        if (c != s.charAt(i)) {
                            match = false;
                            break out;
                        }
                    }

                    match = isMatch(sSubString.substring(i), pSubstring.substring(i), cached);
                    break;
                } else {
                    match = isMatch(sSubString, pSubstring.substring(1), cached);
                    if (match) {
                        break;
                    }
                    for (int i = 1; i < sSubString.length(); i++) {
                        if (isMatch(sSubString.substring(i), pSubstring, cached)) {
                            match = true;
                            break out;
                        }
                    }
                    break;
                }
            }

            if (!match) {
                cached.add(key);
            }

            return match;
        }

        private String mergeDuplicatedStarInPattern(String p) {
            StringBuilder sb = new StringBuilder();
            boolean firstStart = true;
            for (int i = 0; i < p.length(); i++) {
                final char c = p.charAt(i);
                if (c != '*') {
                    sb.append(c);
                    firstStart = true;
                } else {
                    if (firstStart) {
                        sb.append(c);
                        firstStart = false;
                    }
                }
            }
            return sb.toString();
        }
    }
}
