package com.kyc.onboarding.controller;

import com.kyc.onboarding.model.OTPVerificationRequest;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.service.OTPService;
import com.kyc.onboarding.service.UserService;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.servlet.annotation.MultipartConfig;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
    
    @GetMapping("/getotpfordoc/{email}")
    public ResponseEntity<?> getOtpfordoc(@PathVariable String email) {
        

        otpService.generateAndSendOTP(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + email));
    }
    @PostMapping("/verifyotpfordoc")
    public ResponseEntity<?> verifyotpdoc(@RequestParam Integer userId,@RequestParam String otp, MultipartFile aadharImage) {
    	
    	Optional<User> userOpt= userRepository.findById(userId);
    	
    	User user = userOpt.get();
    	
        boolean isVerified = otpService.aadharVerifyOTP(user.getEmail(), otp, aadharImage);
        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid OTP"));
        }
    }
    @PostMapping("/verifyotpforpan")
    public ResponseEntity<?> verifypanotp(@RequestParam Integer userId,@RequestParam String otp, MultipartFile panImage) {
    	
    	Optional<User> userOpt= userRepository.findById(userId);
    	
    	User user = userOpt.get();
    	
        boolean isVerified = otpService.verifyPanOtp(user.getEmail(), otp, panImage);
        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid OTP"));
        }
    }
   
}
