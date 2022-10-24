package com.lzy.learning.demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {
    private Map<Integer, Integer> cachedMatchIndex = new HashMap<>();
    @Test
    void testFindSubstring() {
        final List<Integer> indexes = findSubstring("barfoothefoobarman", new String[]{"foo", "bar"});
        System.out.println(indexes);
    }

    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> indexes = new ArrayList<Integer>();
        int strLen = s.length();
        int wordsSize = words.length;
        if (wordsSize == 0) {
            return indexes;
        }
        int wordLength = words[0].length();
        int matchWordsLength = wordLength * wordsSize;

        for (int i = 0; i <= strLen - matchWordsLength; i++) {
            final boolean match = matchAllWords(s, i, words, wordLength);
            if (match) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    private boolean matchAllWords(String s, int sIndexStart, String[] words, int wordLength) {
        List<Integer> tryWordsIndex = new ArrayList<>(words.length);
        for (int i = 0; i < words.length; i++) {
            tryWordsIndex.add(i);
        }
        int tryIndex = sIndexStart;
        while (!tryWordsIndex.isEmpty()) {
            final int matchedIndex = matchWord(s, tryIndex, words, tryWordsIndex, wordLength);
            if (matchedIndex >= 0) {
                tryWordsIndex.remove(Integer.valueOf(matchedIndex));
                tryIndex += wordLength;
            } else {
                break;
            }
        }
        return tryWordsIndex.isEmpty();
    }

    private int matchWord(String s, int sIndexStart, String[] words, List<Integer> tryWordsIndex, int wordLength) {
        if (cachedMatchIndex.containsKey(sIndexStart)) {
            return cachedMatchIndex.get(sIndexStart);
        }
        int matchWordIndex = -1;


        for (int tryWordIndex : tryWordsIndex) {
            String word = words[tryWordIndex];
            boolean match = true;
            for (int j = 0; j < wordLength; j++) {
                if (s.charAt(sIndexStart + j) != word.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                matchWordIndex = tryWordIndex;
                cachedMatchIndex.put(sIndexStart, matchWordIndex);
                break;
            }
        }

        return matchWordIndex;
    }
}
