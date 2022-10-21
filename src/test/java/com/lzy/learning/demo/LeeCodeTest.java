package com.lzy.learning.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;
import java.util.Stack;

public class LeeCodeTest {
    @ParameterizedTest
    @CsvSource({"'',0", "((),2", "()((),2", ")()()),4", "(()()),6", ")(((((()())()()))()(()))(,22", ")()(((())))(,10", ")(())))(())()),6",})
    void longestValidParenthesesTest(String s, int expected) {
        Assertions.assertEquals(expected, longestValidParentheses(s));
    }

    public int longestValidParentheses(String s) {
        if (Objects.isNull(s) || s.isEmpty()) {
            return 0;
        }

        int[] dp = new int[s.length()];
        int[] end = new int[s.length()];

        dp[0] = 0;
        end[0] = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    if (i - 2 > 0 && s.charAt(i - 2) == ')') {
                        end[i] = end[i - 2] + 2;
                    } else {
                        end[i] = 2;
                    }
                } else if (s.charAt(i - 1) == ')') {
                    end[i] = end[i - 1];
                    if (i - end[i] - 1 >= 0 && s.charAt(i - end[i] - 1) == '(') {
                        end[i] += 2;
                        while (i - end[i] >= 0 && s.charAt(i - end[i]) == ')' && end[i - end[i]] != 0) {
                            end[i] += end[i - end[i]];
                        }
                    } else {
                        end[i] = 0;
                    }
                }
            } else {
                end[i] = 0;
            }
            dp[i] = Math.max(dp[i - 1], end[i]);
        }

        return dp[s.length() - 1];
    }

    class MatchParentheses implements Comparable<MatchParentheses> {
        int left = -1;
        int right = -1;

        public MatchParentheses() {
        }

        public MatchParentheses(int left) {
            this.left = left;
        }

        public MatchParentheses(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public boolean start() {
            return left >= 0;
        }

        public boolean matched() {
            return right > 0;
        }

        public int getLength() {
            return (right >= 1 && left >= 0) ? right - left + 1 : 0;
        }

        @Override
        public int compareTo(MatchParentheses o) {
            return getLength() - o.getLength();
        }

        @Override
        public boolean equals(Object o) {
            if (Objects.isNull(o) || o.getClass() != getClass()) {
                return false;
            }

            MatchParentheses other = (MatchParentheses) o;
            return getLength() - other.getLength() == 0;
        }

        @Override
        public int hashCode() {
            return getLength();
        }
    }

    public class MatchParenthesesStack extends Stack<MatchParentheses> {
        private MatchParentheses max = new MatchParentheses();

        @Override
        public MatchParentheses push(MatchParentheses item) {
            if (empty()) {
                if (max.compareTo(item) < 0) {
                    max = item;
                }
                return super.push(item);
            }
            MatchParentheses peek = peek();
            if (peek.matched() && item.matched() && peek.right + 1 == item.left) {
                peek.right = item.right;
                if (max.compareTo(peek) < 0) {
                    max = peek;
                }
            } else {
                super.push(item);
                if (max.compareTo(item) < 0) {
                    max = item;
                }
            }
            return item;
        }
    }

    public int longestValidParentheses2(String s) {
        MatchParenthesesStack stack = new MatchParenthesesStack();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                MatchParentheses match = new MatchParentheses(i);
                stack.push(match);
            } else {
                MatchParentheses match = null;
                Stack<MatchParentheses> temp = new Stack<>();
                while (!stack.empty()) {
                    match = stack.pop();
                    if (!match.matched()) {
                        break;
                    }
                    temp.push(match);
                    match = null;
                }

                if (Objects.nonNull(match)) {
                    match.right = i;
                    stack.push(match);
                } else {
                    while (!temp.isEmpty()) {
                        stack.push(temp.pop());
                    }

                    while ((i + 1) < s.length() && s.charAt(i + 1) == ')') {
                        i++;
                    }
                }
            }
        }

        if (stack.size() == 0) {
            return 0;
        }

        return stack.max.getLength();
    }
}
