package com.resumescanner.service;

import com.resumescanner.utils.TextProcessor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private final TextProcessor textProcessor;

    public MatchingService(TextProcessor textProcessor) {
        this.textProcessor = textProcessor;
    }

    public double calculateMatchScore(List<String> resumeKeywords, List<String> jobKeywords) {
        if (jobKeywords == null || jobKeywords.isEmpty()) return 0.0;
        if (resumeKeywords == null || resumeKeywords.isEmpty()) return 0.0;

        Set<String> resumeSet = resumeKeywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        long matched = jobKeywords.stream()
                .map(String::toLowerCase)
                .filter(resumeSet::contains)
                .count();

        return Math.round(((double) matched / jobKeywords.size()) * 100.0 * 100.0) / 100.0;
    }

    public List<String> getMatchedKeywords(List<String> resumeKeywords, List<String> jobKeywords) {
        if (resumeKeywords == null || jobKeywords == null) return Collections.emptyList();

        Set<String> resumeSet = resumeKeywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return jobKeywords.stream()
                .map(String::toLowerCase)
                .filter(resumeSet::contains)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getMissingKeywords(List<String> resumeKeywords, List<String> jobKeywords) {
        if (jobKeywords == null) return Collections.emptyList();
        if (resumeKeywords == null) return new ArrayList<>(jobKeywords);

        Set<String> resumeSet = resumeKeywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return jobKeywords.stream()
                .map(String::toLowerCase)
                .filter(kw -> !resumeSet.contains(kw))
                .distinct()
                .collect(Collectors.toList());
    }

    public double calculateCosineSimilarity(String text1, String text2, TextProcessor processor) {
        Map<String, Integer> freq1 = processor.getWordFrequency(text1);
        Map<String, Integer> freq2 = processor.getWordFrequency(text2);

        Set<String> allWords = new HashSet<>();
        allWords.addAll(freq1.keySet());
        allWords.addAll(freq2.keySet());

        double dotProduct = 0.0, magnitude1 = 0.0, magnitude2 = 0.0;

        for (String word : allWords) {
            int v1 = freq1.getOrDefault(word, 0);
            int v2 = freq2.getOrDefault(word, 0);
            dotProduct += (double) v1 * v2;
            magnitude1 += (double) v1 * v1;
            magnitude2 += (double) v2 * v2;
        }

        if (magnitude1 == 0 || magnitude2 == 0) return 0.0;
        return Math.round((dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2))) * 100.0 * 100.0) / 100.0;
    }
}