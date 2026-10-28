package com.aihrms.aihrmsbackend.ai.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ResumeTextExtractor {

    public String extractFromPath(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            return "";
        }
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Resume file not found: " + filePath);
        }
        String name = path.getFileName().toString().toLowerCase();
        if (name.endsWith(".pdf")) {
            return extractPdf(path);
        }
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public String extractPdf(Path path) throws IOException {
        try (PDDocument doc = Loader.loadPDF(path.toFile())) {
            return new PDFTextStripper().getText(doc);
        }
    }

    public String guessMimeType(String filePath) {
        if (filePath == null) {
            return "application/octet-stream";
        }
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".txt") || lower.endsWith(".md")) {
            return "text/plain";
        }
        return "application/octet-stream";
    }
}