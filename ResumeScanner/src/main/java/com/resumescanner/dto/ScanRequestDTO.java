package com.resumescanner.dto;

import org.springframework.web.multipart.MultipartFile;

public class ScanRequestDTO {

    private MultipartFile resumeFile;
    private String resumeText;
    private String jobDescription;

    public MultipartFile getResumeFile() { return resumeFile; }
    public String getResumeText() { return resumeText; }
    public String getJobDescription() { return jobDescription; }

    public void setResumeFile(MultipartFile resumeFile) { this.resumeFile = resumeFile; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
}