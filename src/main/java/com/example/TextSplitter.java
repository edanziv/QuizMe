package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextSplitter {
    public static List<String> splitByLength(String text, int maxWords) {
        String[] words = text.split("\\s+");    // split text by whitespace
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < words.length; i += maxWords) {
            int end = Math.min(words.length, i + maxWords);
            chunks.add(String.join(" ", Arrays.copyOfRange(words, i, end)));
        }
        return chunks;
    }
}

