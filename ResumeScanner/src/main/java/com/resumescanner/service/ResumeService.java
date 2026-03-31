package com.resumescanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.resumescanner.dto.ScanResultDTO;
import com.resumescanner.entity.ResumeScan;
import com.resumescanner.repository.ResumeScanRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ResumeService {

    @Autowired
    private ResumeScanRepository resumeScanRepository;

    @Autowired
    private PDFParsingService pdfParsingService;

    /**
     * Scan a resume file against a job description
     */
    public ScanResultDTO scanResume(MultipartFile file, String jobDescription) throws Exception {
        
        if (file == null || file.isEmpty()) {
            throw new Exception("File is empty");
        }
        
        // Extract text based on file type
        String resumeText;
        String filename = file.getOriginalFilename().toLowerCase();
        
        if (filename.endsWith(".pdf")) {
            resumeText = pdfParsingService.extractTextFromPDF(file);
        } else if (filename.endsWith(".docx")) {
            resumeText = pdfParsingService.extractTextFromDOCX(file);
        } else {
            throw new Exception("Unsupported file format. Please upload PDF or DOCX files only.");
        }
        
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new Exception("Could not extract text from file. Please ensure the file is not empty.");
        }
        
        // Calculate match result
        MatchResult matchResult = calculateMatch(resumeText, jobDescription);

        // Create and save scan record
        ResumeScan scan = new ResumeScan();
        scan.setFileName(file.getOriginalFilename());
        scan.setFileType(file.getContentType());
        scan.setResumeText(resumeText);
        scan.setJobDescription(jobDescription);
        scan.setMatchScore(matchResult.getMatchScore().doubleValue());
        scan.setMatchedKeywords(matchResult.getMatchedKeywords());
        scan.setMissingKeywords(matchResult.getMissingKeywords());
        scan.setScannedAt(LocalDateTime.now());

        ResumeScan savedScan = resumeScanRepository.save(scan);

        // Return as DTO
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new ScanResultDTO(
            savedScan.getId(),
            savedScan.getFileName(),
            savedScan.getFileType(),
            savedScan.getResumeText(),
            savedScan.getJobDescription(),
            savedScan.getMatchScore().floatValue(),
            savedScan.getMatchedKeywords(),
            savedScan.getMissingKeywords(),
            savedScan.getScannedAt().format(formatter)
        );
    }

    /**
     * Calculate match score and keywords
     */
    private MatchResult calculateMatch(String resumeText, String jobDescription) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            return new MatchResult(0f, "No matched keywords", "Error: Could not extract text from resume");
        }
        
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return new MatchResult(0f, "No matched keywords", "Error: Job description is empty");
        }
        
        String resumeLower = resumeText.toLowerCase();
        String jobLower = jobDescription.toLowerCase();

        // Extract keywords from job description
        String[] jobKeywords = extractKeywords(jobLower);
        
        if (jobKeywords.length == 0) {
            return new MatchResult(0f, "No matched keywords", "Could not extract keywords from job description");
        }

        // Find matched and missing keywords
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String keyword : jobKeywords) {
            if (resumeLower.contains(keyword)) {
                matched.add(keyword);
            } else {
                missing.add(keyword);
            }
        }

        // Calculate match score (0-100)
        Float matchScore = 0f;
        if (jobKeywords.length > 0) {
            matchScore = (float) ((matched.size() * 100) / (float) jobKeywords.length);
        }

        // Build result
        String matchedKeywords = matched.isEmpty() ? "No matched keywords" : String.join(", ", matched);
        String missingKeywords = missing.isEmpty() ? "No missing keywords" : String.join(", ", missing);

        return new MatchResult(matchScore, matchedKeywords, missingKeywords);
    }

    /**
     * Extract keywords from text
     */
    private String[] extractKeywords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new String[0];
        }
        
        String textLower = text.toLowerCase();
        
        // Split by whitespace and punctuation, but preserve tech keywords
        String[] words = textLower.split("\\b");
        
        List<String> keywords = new ArrayList<>();
        Set<String> commonWords = new HashSet<>(Arrays.asList(
            "the", "and", "or", "for", "with", "from", "to", "in", "of", "is", 
            "are", "was", "were", "be", "been", "being", "have", "has", "had",
            "do", "does", "did", "will", "would", "could", "should", "may", "might",
            "can", "a", "an", "as", "at", "by", "it", "on", "this", "that", "your",
            "you", "we", "they", "them", "their", "what", "which", "who", "how", "when"
        ));

        for (String word : words) {
            // Clean up the word but preserve numbers and basic characters
            String cleaned = word.trim();
            if (cleaned.isEmpty()) continue;
            
            // Remove leading/trailing special characters but keep them in the middle
            cleaned = cleaned.replaceAll("^[^a-z0-9]+|[^a-z0-9]+$", "");
            
            // Include words >= 2 chars, not in common words, and not just numbers
            if (cleaned.length() >= 2 && 
                !commonWords.contains(cleaned) && 
                !cleaned.matches("^\\d+$")) {
                keywords.add(cleaned);
            }
        }

        // Remove duplicates while preserving order
        LinkedHashSet<String> uniqueKeywords = new LinkedHashSet<>(keywords);
        return uniqueKeywords.toArray(new String[0]);
    }

    /**
     * Get all scanned resumes
     */
    public List<ScanResultDTO> getAllScans() {
        List<ResumeScan> scans = resumeScanRepository.findAll();
        List<ScanResultDTO> dtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (ResumeScan scan : scans) {
            dtos.add(new ScanResultDTO(
                scan.getId(),
                scan.getFileName(),
                scan.getFileType(),
                scan.getResumeText(),
                scan.getJobDescription(),
                scan.getMatchScore().floatValue(),
                scan.getMatchedKeywords(),
                scan.getMissingKeywords(),
                scan.getScannedAt().format(formatter)
            ));
        }

        return dtos;
    }

    /**
     * Get scan by ID
     */
    public ScanResultDTO getScanById(Long id) {
        ResumeScan scan = resumeScanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Scan not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new ScanResultDTO(
            scan.getId(),
            scan.getFileName(),
            scan.getFileType(),
            scan.getResumeText(),
            scan.getJobDescription(),
            scan.getMatchScore().floatValue(),
            scan.getMatchedKeywords(),
            scan.getMissingKeywords(),
            scan.getScannedAt().format(formatter)
        );
    }

    /**
     * Delete scan by ID
     */
    public void deleteScan(Long id) {
        resumeScanRepository.deleteById(id);
    }

    /**
     * Inner class to hold match results
     */
    private static class MatchResult {
        private Float matchScore;
        private String matchedKeywords;
        private String missingKeywords;

        public MatchResult(Float matchScore, String matchedKeywords, String missingKeywords) {
            this.matchScore = matchScore;
            this.matchedKeywords = matchedKeywords;
            this.missingKeywords = missingKeywords;
        }

        public Float getMatchScore() {
            return matchScore;
        }

        public String getMatchedKeywords() {
            return matchedKeywords;
        }

        public String getMissingKeywords() {
            return missingKeywords;
        }
    }
}