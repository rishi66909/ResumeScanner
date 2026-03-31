package com.resumescanner.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.List;

@Service
public class PDFParsingService {

    public String extractTextFromPDF(MultipartFile file) throws Exception {
        StringBuilder text = new StringBuilder();
        
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String pdfText = stripper.getText(document);
            
            if (pdfText != null) {
                text.append(pdfText);
            }
        } catch (Exception e) {
            System.err.println("Error extracting PDF: " + e.getMessage());
            throw new Exception("Failed to extract text from PDF: " + e.getMessage());
        }
        
        return text.toString();
    }

    public String extractTextFromDOCX(MultipartFile file) throws Exception {
        StringBuilder text = new StringBuilder();

        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String paraText = paragraph.getText();
                if (paraText != null && !paraText.trim().isEmpty()) {
                    text.append(paraText).append("\n");
                }
            }
        }

        return text.toString();
    }
}