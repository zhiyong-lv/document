package com.lzy.learning.leecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupAnagramsNo49 {
}

class Solution49 {
    public List<List<String>> groupAnagrams(String[] strs) {
        return new ArrayList<>(Arrays.stream(strs).collect(Collectors.groupingBy(
                s -> {
                    final char[] charArr = s.toCharArray();
                    Arrays.sort(charArr);
                    return new String(charArr);
                }
        )).values());

        //
        // Map<String, List<String>> groups = new HashMap<>();
        //
        // for (String s : strs) {
        //     final String key = charCount(s);
        //     List<String> l = groups.get(key);
        //     if (Objects.isNull(l)) {
        //         l = new ArrayList<>();
        //         groups.put(key, l);
        //     }
        //     l.add(s);
        // }
        // List<List<String>> result = new ArrayList<>(groups.values());
        // if (result.size() > 2) {
        //     result.sort(Comparator.comparingInt(List::size));
        // }
        // for (List<String> subList : result) {
        //     if (subList.size() > 2) {
        //         subList.sort((sl1, sl2) -> {
        //             int cmpRst = 0;
        //             for (int charAt = 0; charAt < sl1.length(); charAt++) {
        //                 cmpRst = sl1.charAt(charAt) - sl2.charAt(charAt);
        //                 if (cmpRst != 0) {
        //                     break;
        //                 }
        //             }
        //             return cmpRst;
        //         });
        //     }
        // }
        // return result;
    }

    private String charCount(String word) {
        int[] count = getCountArr();
        for (int i = 0; i < word.length(); i++) {
            final int index = word.charAt(i) - 'a';
            count[index] += 1;
        }
        for (int i = 0; i < count.length; i++) {

        }
        return Arrays.toString(count);
    }

    private int[] getCountArr() {
        int[] count = new int[26];
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }
        return count;
    }
}
