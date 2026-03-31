package com.resumescanner.dto;

public class ScanResultDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String resumeText;
    private String jobDescription;
    private Float matchScore;
    private String matchedKeywords;
    private String missingKeywords;
    private String scannedAt;

    // Constructors
    public ScanResultDTO() {}

    public ScanResultDTO(Long id, String fileName, String fileType, 
                        String resumeText, String jobDescription,
                        Float matchScore, String matchedKeywords, 
                        String missingKeywords, String scannedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.resumeText = resumeText;
        this.jobDescription = jobDescription;
        this.matchScore = matchScore;
        this.matchedKeywords = matchedKeywords;
        this.missingKeywords = missingKeywords;
        this.scannedAt = scannedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public Float getMatchScore() { return matchScore; }
    public void setMatchScore(Float matchScore) { this.matchScore = matchScore; }

    public String getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(String matchedKeywords) { this.matchedKeywords = matchedKeywords; }

    public String getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(String missingKeywords) { this.missingKeywords = missingKeywords; }

    public String getScannedAt() { return scannedAt; }
    public void setScannedAt(String scannedAt) { this.scannedAt = scannedAt; }

    @Override
    public String toString() {
        return "ScanResultDTO{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", matchScore=" + matchScore +
                ", matchedKeywords='" + matchedKeywords + '\'' +
                ", missingKeywords='" + missingKeywords + '\'' +
                ", scannedAt='" + scannedAt + '\'' +
                '}';
    }
}