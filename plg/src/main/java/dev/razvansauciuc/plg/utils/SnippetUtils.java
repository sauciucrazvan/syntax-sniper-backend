package dev.razvansauciuc.plg.utils;

import java.util.Random;

public class SnippetUtils {

    public static String getRandomSnippet(String fullCode, int snippetLength) {
        String[] lines = fullCode.split("\n");
        if (lines.length <= snippetLength) return fullCode;

        int start = new Random().nextInt(lines.length - snippetLength);
        StringBuilder snippet = new StringBuilder();
        for (int i = start; i < start + snippetLength; i++) {
            snippet.append(lines[i]).append("\n");
        }
        return snippet.toString();
    }
}
