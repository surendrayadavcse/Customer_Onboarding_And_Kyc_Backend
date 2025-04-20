package com.kyc.onboarding.controller;

import com.kyc.onboarding.model.OTPVerificationRequest;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.service.OTPService;
import com.kyc.onboarding.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class OTPController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getOTP/{email}")
    public ResponseEntity<?> getOtp(@PathVariable String email, @RequestParam String mobile) {
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(400).body(Map.of("message", "User already registered with this email"));
        }

        if (userRepository.findByMobile(mobile).isPresent()) {
            return ResponseEntity.status(400).body(Map.of("message", "User already registered with this mobile number"));
        }

        otpService.generateAndSendOTP(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + email));
    }


    // Endpoint to verify the OTP entered by the user
    @PostMapping("/verifyOTP")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPVerificationRequest otpVerificationRequest) {
        boolean isVerified = otpService.verifyOTP(otpVerificationRequest.getEmail(), otpVerificationRequest.getOtp());
        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid OTP"));
        }
    }
}
