package com.resumescanner.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScanResponseDTO {

    private Long id;
    private String fileName;
    private double matchScore;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private List<String> extractedKeywords;
    private String summary;
    private LocalDateTime scannedAt;

    // --- Getters ---
    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public double getMatchScore() { return matchScore; }
    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public List<String> getMissingKeywords() { return missingKeywords; }
    public List<String> getExtractedKeywords() { return extractedKeywords; }
    public String getSummary() { return summary; }
    public LocalDateTime getScannedAt() { return scannedAt; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
    public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }
    public void setExtractedKeywords(List<String> extractedKeywords) { this.extractedKeywords = extractedKeywords; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setScannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ScanResponseDTO dto = new ScanResponseDTO();

        public Builder id(Long id) { dto.id = id; return this; }
        public Builder fileName(String fileName) { dto.fileName = fileName; return this; }
        public Builder matchScore(double matchScore) { dto.matchScore = matchScore; return this; }
        public Builder matchedKeywords(List<String> matchedKeywords) { dto.matchedKeywords = matchedKeywords; return this; }
        public Builder missingKeywords(List<String> missingKeywords) { dto.missingKeywords = missingKeywords; return this; }
        public Builder extractedKeywords(List<String> extractedKeywords) { dto.extractedKeywords = extractedKeywords; return this; }
        public Builder summary(String summary) { dto.summary = summary; return this; }
        public Builder scannedAt(LocalDateTime scannedAt) { dto.scannedAt = scannedAt; return this; }
        public ScanResponseDTO build() { return dto; }
    }
}