package com.lzy.learning.leecode.str;

import java.util.LinkedList;

public class StrRelated {
    public static void main(String[] args) {
        System.out.printf("Result is %s:\n", new Solution459NewSolution2().repeatedSubstringPattern("ab"));
    }
}

class Solution459NewSolution2 {
    int[] getNext(String s) {
        final int[] next = new int[s.length()];
        for (int preIdx = 0, j = 1; j < next.length; j++) {
            while (preIdx > 0 && s.charAt(j) != s.charAt(preIdx)) {
                preIdx = next[preIdx - 1];
            }
            if (s.charAt(j) == s.charAt(preIdx)) {
                preIdx++;
                next[j] = preIdx;
            }
        }
        return next;
    }

    public boolean repeatedSubstringPattern(String s) {
        if (s.length() == 1) {
            return false;
        }
        int[] next = new int[s.length()];
        int maxMatchedLen = 0;
        for (int preIdx = 0, i = 1; i < s.length(); i++) {
            while (preIdx > 0 && s.charAt(preIdx) != s.charAt(i)) {
                preIdx = next[preIdx - 1];
            }
            if (s.charAt(preIdx) == s.charAt(i)) {
                preIdx++;
                next[i] = preIdx;
                maxMatchedLen = Math.max(maxMatchedLen, preIdx);
            }
        }
        return maxMatchedLen > 0 && s.length() % (s.length() - maxMatchedLen) == 0 && next[s.length() - 1] == maxMatchedLen;
    }
}

class Solution459 {
    public boolean repeatedSubstringPattern(String s) {
        for (int i = 1; i <= s.length() / 2; i++) {
            if (s.equals(s.substring(i) + s.substring(0, i))) {
                return true;
            }
        }
        return false;
    }
}

class Solution459NewSolution {
    public boolean repeatedSubstringPattern(String s) {
        boolean match = false;
        for (int step = 1; step <= s.length() / 2; step++) {
            if (s.length() % step != 0) {
                continue;
            }
            for (int i = step; i < s.length(); i++) {
                if (s.charAt(i - step) != s.charAt(i)) {
                    match = false;
                    break;
                } else {
                    match = true;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }
}

class SolutionSubStringNewSolution {
    public int strStr(String haystack, String needle) {
        int[] back = getMaxSamePreLengthTbl(needle);
        for (int i = 0, matchIdx = 0; i <= haystack.length(); ) {
            if (haystack.charAt(i) == needle.charAt(matchIdx)) {
                i++;
                matchIdx++;
                if (matchIdx == needle.length()) {
                    return i - matchIdx;
                }
            } else if (matchIdx > 0) {
                matchIdx = back[matchIdx - 1];
            } else {
                i++;
                if (i > haystack.length() - needle.length()) {
                    break;
                }
            }
        }
        return -1;
    }

    int[] getMaxSamePreLengthTbl(String needle) {
        int[] rst = new int[needle.length()];
        for (int i = 1, matchIdx = 0; i < needle.length(); ) {
            while (needle.charAt(i++) == needle.charAt(matchIdx++)) {
                rst[i - 1] = matchIdx;
            }
            matchIdx = 0;
        }
        return rst;
    }
}

/**
 * 给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串的第一个匹配项的下标（下标从 0 开始）。如果 needle 不是 haystack 的一部分，则返回  -1 。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：haystack = "sadbutsad", needle = "sad"
 * 输出：0
 * 解释："sad" 在下标 0 和 6 处匹配。
 * 第一个匹配项的下标是 0 ，所以返回 0 。
 * 示例 2：
 * <p>
 * 输入：haystack = "leetcode", needle = "leeto"
 * 输出：-1
 * 解释："leeto" 没有在 "leetcode" 中出现，所以返回 -1 。
 *  
 * <p>
 * 提示：
 * <p>
 * 1 <= haystack.length, needle.length <= 104
 * haystack 和 needle 仅由小写英文字符组成
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode.cn/problems/find-the-index-of-the-first-occurrence-in-a-string
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
class SolutionSubString {
    public int strStr(String haystack, String needle) {
        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            if (haystack.charAt(i) == needle.charAt(0)) {
                if (haystack.substring(i, i + needle.length()).equals(needle)) {
                    return i;
                }
            }
        }
        return -1;
    }
}

class SolutionLeftRotation {
    public String leftRotation(final String s, final int k) {
        int len = s.length();
        int rightPart = len - k;
        int restRotationStepOneCount = k - rightPart % k;

        final char[] chars = s.toCharArray();
        for (int i = k; i < chars.length; i++) {
            char c = chars[i - k];
            chars[i - k] = chars[i];
            chars[i] = c;
        }
        for (int i = 0; i < restRotationStepOneCount; i++) {
            char c = chars[len - 1];
            for (int j = 1; j < k; j++) {
                char t = chars[len - j - 1];
                chars[len - j - 1] = c;
                c = t;
            }
            chars[len - 1] = c;
        }
        return new String(chars);
    }
}

/**
 * 给你一个字符串 s ，请你反转字符串中 单词 的顺序。
 * <p>
 * 单词 是由非空格字符组成的字符串。s 中使用至少一个空格将字符串中的 单词 分隔开。
 * <p>
 * 返回 单词 顺序颠倒且 单词 之间用单个空格连接的结果字符串。
 * <p>
 * 注意：输入字符串 s中可能会存在前导空格、尾随空格或者单词间的多个空格。返回的结果字符串中，单词间应当仅用单个空格分隔，且不包含任何额外的空格。
 * <p>
 * 示例 1：
 * <p>
 * 输入：s = "the sky is blue"
 * 输出："blue is sky the"
 * 示例 2：
 * <p>
 * 输入：s = "  hello world  "
 * 输出："world hello"
 * 解释：反转后的字符串中不能存在前导空格和尾随空格。
 * 示例 3：
 * <p>
 * 输入：s = "a good   example"
 * 输出："example good a"
 * 解释：如果两个单词间有多余的空格，反转后的字符串需要将单词间的空格减少到仅有一个。
 * 提示：
 * <p>
 * 1 <= s.length <= 104
 * s 包含英文大小写字母、数字和空格 ' '
 * s 中 至少存在一个
 * <p>
 * 单词进阶：如果字符串在你使用的编程语言中是一种可变数据类型，请尝试使用 O(1) 额外空间复杂度的 原地 解法。
 */
class Solution151 {
    public String reverseWords(String s) {
        LinkedList<String> list = new LinkedList<>();

        boolean found = false;
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (' ' == c) {
                if (found) {
                    end = i;
                    found = false;
                    list.addFirst(s.substring(start, end));
                }
            } else {
                if (!found) {
                    start = i;
                    found = true;
                }
            }
        }

        if (found) {
            list.addFirst(s.substring(start));
        }
        return String.join(" ", list);
    }
}

/**
 * 给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于 val 的元素，并返回移除后数组的新长度。
 * <p>
 * 不要使用额外的数组空间，你必须仅使用 O(1) 额外空间并 原地 修改输入数组。
 * <p>
 * 元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。
 * <p>
 * 说明:
 * <p>
 * 为什么返回数值是整数，但输出的答案是数组呢?
 * <p>
 * 请注意，输入数组是以「引用」方式传递的，这意味着在函数里修改输入数组对于调用者是可见的。
 * <p>
 * 你可以想象内部操作如下:
 * <p>
 * // nums 是以“引用”方式传递的。也就是说，不对实参作任何拷贝
 * int len = removeElement(nums, val);
 * <p>
 * // 在函数里修改输入数组对于调用者是可见的。
 * // 根据你的函数返回的长度, 它会打印出数组中 该长度范围内 的所有元素。
 * for (int i = 0; i < len; i++) {
 * print(nums[i]);
 * }
 * 示例 1：
 * <p>
 * 输入：nums = [3,2,2,3], val = 3
 * 输出：2, nums = [2,2]
 * 解释：函数应该返回新的长度 2, 并且 nums 中的前两个元素均为 2。你不需要考虑数组中超出新长度后面的元素。例如，函数返回的新长度为 2 ，而 nums = [2,2,3,3] 或 nums = [2,2,0,0]，也会被视作正确答案。
 * 示例 2：
 * <p>
 * 输入：nums = [0,1,2,2,3,0,4,2], val = 2
 * 输出：5, nums = [0,1,4,0,3]
 * 解释：函数应该返回新的长度 5, 并且 nums 中的前五个元素为 0, 1, 3, 0, 4。注意这五个元素可为任意顺序。你不需要考虑数组中超出新长度后面的元素。
 */
class Solution27 {
    public int removeElement(int[] nums, int val) {
        int newLen = 0;
        for (int i = 0; i < nums.length; i++) {
            if (val != nums[i]) {
                nums[newLen++] = nums[i];
            }
        }
        return newLen;
    }
}

class SolutionMergeAllSpace {
    public String mergeSpaces(final String s) {
        StringBuffer buffer = new StringBuffer();
        boolean addSpace = false;
        for (char c : s.toCharArray()) {
            final boolean notSpace = ' ' != c;
            buffer.append((notSpace || addSpace) ? c : "");
            addSpace = notSpace;
        }
        return buffer.toString().trim();
    }
}

class SolutionOffer05 {
    public String replaceSpaces(final String s) {
        StringBuffer buffer = new StringBuffer();
        for (char c : s.toCharArray()) {
            buffer.append((' ' == c) ? "%20" : c);
        }
        return buffer.toString();
    }
}

/**
 * 给定一个字符串 s 和一个整数 k，从字符串开头算起，每计数至 2k 个字符，就反转这 2k 字符中的前 k 个字符。
 * <p>
 * 如果剩余字符少于 k 个，则将剩余字符全部反转。
 * 如果剩余字符小于 2k 但大于或等于 k 个，则反转前 k 个字符，其余字符保持原样。
 * 示例 1：
 * <p>
 * 输入：s = "abcdefg", k = 2
 * 输出："bacdfeg"
 * 示例 2：
 * <p>
 * 输入：s = "abcd", k = 2
 * 输出："bacd"
 * 提示：
 * <p>
 * 1 <= s.length <= 104
 * s 仅由小写英文组成
 * 1 <= k <= 104
 */
class Solution541 {
    public String reverseStr(String s, int k) {
        final char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i % (2 * k) == 2 * k - 1) {
                reverseString(chars, i - 2 * k + 1, i - k);
            } else if (i == chars.length - 1) {
                if (i % (2 * k) < k) {
                    reverseString(chars, i - i % (2 * k), chars.length - 1);
                } else {
                    reverseString(chars, i - i % (2 * k), i - i % (2 * k) + k - 1);
                }
            }
        }
        return new String(chars);
    }

    void reverseString(char[] s, int start, int end) {
        for (int i = start, idxReverse = end; i < idxReverse; i++, idxReverse--) {
            char temp = s[i];
            s[i] = s[idxReverse];
            s[idxReverse] = temp;
        }
    }
}

/**
 * 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 s 的形式给出。
 * <p>
 * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
 * <p>
 * 示例 1：
 * <p>
 * 输入：s = ["h","e","l","l","o"]
 * 输出：["o","l","l","e","h"]
 * 示例 2：
 * <p>
 * 输入：s = ["H","a","n","n","a","h"]
 * 输出：["h","a","n","n","a","H"]
 * 提示：
 * <p>
 * 1 <= s.length <= 105
 * s[i] 都是 ASCII 码表中的可打印字符
 */
class Solution344 {
    public void reverseString(char[] s) {
        for (int i = 0, idxReverse = s.length - 1 - i; i < idxReverse; i++, idxReverse--) {
            char temp = s[i];
            s[i] = s[idxReverse];
            s[idxReverse] = temp;
        }
    }
}