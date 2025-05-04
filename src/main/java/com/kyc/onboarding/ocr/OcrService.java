package com.kyc.onboarding.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class OcrService {

    // Extract text from the uploaded image file
    public String extractTextFromImage(MultipartFile image) {
        Tesseract tesseract = new Tesseract();

        try {
            // Fetch tessdata directory from classpath
            URL tessDataUrl = getClass().getClassLoader().getResource("testdata");
            if (tessDataUrl == null) {
                throw new RuntimeException("tessdata folder not found in resources");
            }	

            tesseract.setDatapath(new File(tessDataUrl.toURI()).getAbsolutePath());
            tesseract.setLanguage("eng");

            // Convert MultipartFile to File
            File tempFile = convertMultipartFileToFile(image);

            try {
                // Perform OCR on the image
                return tesseract.doOCR(tempFile);
            } 
            
            finally {
                if (!tempFile.delete()) {
                    LoggerFactory.getLogger(getClass()).warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                }
            }
        }
         catch (TesseractException e) {
            throw new RuntimeException("OCR processing failed: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to File: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // Helper: Convert MultipartFile to temporary File
    private File convertMultipartFileToFile(MultipartFile image) throws IOException {
        Path tempFilePath = Files.createTempFile("ocr-", image.getOriginalFilename());
        Files.copy(image.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        return tempFilePath.toFile();
    }
}