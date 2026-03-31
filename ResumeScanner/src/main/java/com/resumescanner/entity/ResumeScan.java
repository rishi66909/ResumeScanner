package com.resumescanner.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_scans")
public class ResumeScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "match_score")
    private Double matchScore;

    @Column(columnDefinition = "TEXT")
    private String matchedKeywords;

    @Column(columnDefinition = "TEXT")
    private String missingKeywords;

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @PrePersist
    protected void onCreate() {
        scannedAt = LocalDateTime.now();
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public String getResumeText() { return resumeText; }
    public String getJobDescription() { return jobDescription; }
    public Double getMatchScore() { return matchScore; }
    public String getMatchedKeywords() { return matchedKeywords; }
    public String getMissingKeywords() { return missingKeywords; }
    public LocalDateTime getScannedAt() { return scannedAt; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public void setMatchedKeywords(String matchedKeywords) { this.matchedKeywords = matchedKeywords; }
    public void setMissingKeywords(String missingKeywords) { this.missingKeywords = missingKeywords; }
    public void setScannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ResumeScan scan = new ResumeScan();

        public Builder id(Long id) { scan.id = id; return this; }
        public Builder fileName(String fileName) { scan.fileName = fileName; return this; }
        public Builder fileType(String fileType) { scan.fileType = fileType; return this; }
        public Builder resumeText(String resumeText) { scan.resumeText = resumeText; return this; }
        public Builder jobDescription(String jobDescription) { scan.jobDescription = jobDescription; return this; }
        public Builder matchScore(Double matchScore) { scan.matchScore = matchScore; return this; }
        public Builder matchedKeywords(String matchedKeywords) { scan.matchedKeywords = matchedKeywords; return this; }
        public Builder missingKeywords(String missingKeywords) { scan.missingKeywords = missingKeywords; return this; }
        public ResumeScan build() { return scan; }
    }
}