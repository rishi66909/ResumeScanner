package com.resumescanner.utils;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TextProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "in", "on", "at", "to", "for",
            "of", "with", "by", "from", "is", "are", "was", "were", "be", "been",
            "being", "have", "has", "had", "do", "does", "did", "will", "would",
            "could", "should", "may", "might", "shall", "can", "need", "dare",
            "ought", "used", "it", "its", "this", "that", "these", "those", "i",
            "me", "my", "we", "our", "you", "your", "he", "his", "she", "her",
            "they", "their", "what", "which", "who", "whom", "when", "where",
            "why", "how", "all", "both", "each", "few", "more", "most", "other",
            "some", "such", "than", "too", "very", "just", "as", "if", "so"
    ));

    public String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s+#.]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();
        String normalized = normalize(text);
        return Arrays.stream(normalized.split("\\s+"))
                .filter(word -> word.length() > 1)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getWordFrequency(String text) {
        List<String> tokens = tokenize(text);
        Map<String, Integer> freq = new HashMap<>();
        for (String token : tokens) {
            freq.merge(token, 1, Integer::sum);
        }
        return freq;
    }

    public Set<String> extractNgrams(String text, int n) {
        List<String> tokens = tokenize(text);
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= tokens.size() - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (j > 0) sb.append(" ");
                sb.append(tokens.get(i + j));
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    public boolean containsPhrase(String text, String phrase) {
        if (text == null || phrase == null) return false;
        return text.toLowerCase().contains(phrase.toLowerCase());
    }

    public String sanitize(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
                   .trim();
    }
}