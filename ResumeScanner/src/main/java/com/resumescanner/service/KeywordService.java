package com.resumescanner.service;

import com.resumescanner.utils.TextProcessor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeywordService {

    private final TextProcessor textProcessor;

    // Common tech/professional keywords to boost extraction
    private static final Set<String> TECH_KEYWORDS = new HashSet<>(Arrays.asList(
            "java", "python", "javascript", "typescript", "react", "angular", "vue",
            "spring", "springboot", "hibernate", "jpa", "rest", "api", "microservices",
            "docker", "kubernetes", "aws", "azure", "gcp", "ci/cd", "devops", "git",
            "sql", "nosql", "mongodb", "postgresql", "mysql", "redis", "kafka",
            "maven", "gradle", "junit", "mockito", "tdd", "agile", "scrum",
            "machine learning", "deep learning", "nlp", "data science", "tensorflow",
            "pytorch", "hadoop", "spark", "elasticsearch", "graphql", "node.js",
            "html", "css", "linux", "bash", "jenkins", "terraform", "ansible",
            "c++", "c#", "golang", "rust", "swift", "kotlin", "scala",
            "communication", "leadership", "teamwork", "problem-solving", "analytical",
            "project management", "agile", "scrum", "kanban", "jira", "confluence"
    ));

    public KeywordService(TextProcessor textProcessor) {
        this.textProcessor = textProcessor;
    }

    public List<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();

        Set<String> keywords = new LinkedHashSet<>();
        String lowerText = text.toLowerCase();

        // Match known tech keywords first (including multi-word)
        for (String kw : TECH_KEYWORDS) {
            if (textProcessor.containsPhrase(lowerText, kw)) {
                keywords.add(kw);
            }
        }

        // Add high-frequency single tokens
        Map<String, Integer> freq = textProcessor.getWordFrequency(text);
        freq.entrySet().stream()
                .filter(e -> e.getValue() >= 2 && e.getKey().length() > 3)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(30)
                .map(Map.Entry::getKey)
                .forEach(keywords::add);

        // Add bigrams
        Set<String> bigrams = textProcessor.extractNgrams(text, 2);
        bigrams.stream()
                .filter(bg -> TECH_KEYWORDS.contains(bg))
                .forEach(keywords::add);

        return new ArrayList<>(keywords);
    }

    public List<String> extractFromJobDescription(String jobDescription) {
        return extractKeywords(jobDescription);
    }
}