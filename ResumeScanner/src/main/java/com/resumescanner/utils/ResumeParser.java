package com.resumescanner.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ResumeParser {

    public String parse(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name is null");
        }

        String lower = originalFilename.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return parsePdf(file.getInputStream());
        } else if (lower.endsWith(".docx")) {
            return parseDocx(file.getInputStream());
        } else if (lower.endsWith(".txt")) {
            return parseTxt(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + originalFilename);
        }
    }

    private String parsePdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {  // PDFBox 2.x API
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String parseDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder sb = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                sb.append(paragraph.getText()).append("\n");
            }
            return sb.toString();
        }
    }

    private String parseTxt(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes());
    }
}