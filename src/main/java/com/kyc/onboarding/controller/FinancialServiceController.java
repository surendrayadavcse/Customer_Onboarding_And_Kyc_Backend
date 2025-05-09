package com.kyc.onboarding.controller;

import com.kyc.onboarding.model.FinancialService;
import com.kyc.onboarding.service.FinancialServiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
public class FinancialServiceController {

    @Autowired
    private FinancialServiceService financialServiceService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @PostMapping("/add")
    public ResponseEntity<?> addService(
            @RequestParam String serviceName,
            @RequestParam String serviceDetails,
            @RequestParam(value = "icon", required = false) MultipartFile iconFile
    ) 
    {
        FinancialService service = new FinancialService();
        service.setServiceName(serviceName);
        service.setServiceDetails(serviceDetails);

        if (iconFile != null && !iconFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + iconFile.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;

            File dest = new File(filePath);
            try {
                iconFile.transferTo(dest);
                service.setServiceIconPath("uploads/" + fileName);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to upload icon: " + e.getMessage());
            }
        }

        FinancialService savedService = financialServiceService.addService(service);
        return ResponseEntity.ok(savedService);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllServices(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
        List<FinancialService> services = financialServiceService.getAllServices(baseUrl);
        return ResponseEntity.ok(services);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Integer id) {
        financialServiceService.deleteServiceById(id);
        return ResponseEntity.ok("Service with ID " + id + " deleted successfully.");
    }
}
