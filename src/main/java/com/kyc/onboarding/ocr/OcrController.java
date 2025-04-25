package com.kyc.onboarding.ocr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    // Endpoint to process the uploaded image and extract text
    @PostMapping("/extracttext")
    public ResponseEntity<String> extractTextFromImage(@RequestParam("image") MultipartFile image) {
        try {
            // Call the OcrService to extract text from the uploaded image
            String extractedText = ocrService.extractTextFromImage(image);
            return ResponseEntity.ok(extractedText);  // Return the extracted text as the response
        } catch (Exception e) {
            return ResponseEntity.status(500).body("OCR processing failed: " + e.getMessage());
        }
    }
}
