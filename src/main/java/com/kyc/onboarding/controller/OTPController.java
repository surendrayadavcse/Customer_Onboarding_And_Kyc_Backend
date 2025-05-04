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

    private final UserRepository userRepository;
    private final OTPService otpService;

    // Constants defined inside the same component
    private static final String MESSAGE = "message";
    private static final String EMAIL_ALREADY_REGISTERED = "User already registered with this email";
    private static final String MOBILE_ALREADY_REGISTERED = "User already registered with this mobile number";
    private static final String OTP_SENT = "OTP sent to ";
    private static final String OTP_VERIFIED = "OTP verified successfully";
    private static final String INVALID_OTP = "Invalid OTP";

    public OTPController(UserRepository userRepository, OTPService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    @GetMapping("/getOTP/{email}")
    public ResponseEntity<?> getOtp(@PathVariable String email, @RequestParam String mobile) {
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(400).body(Map.of(MESSAGE, EMAIL_ALREADY_REGISTERED));
        }
        if (userRepository.findByMobile(mobile).isPresent()) {
            return ResponseEntity.status(400).body(Map.of(MESSAGE, MOBILE_ALREADY_REGISTERED));
        }
        otpService.generateAndSendOTP(email);
        return ResponseEntity.ok(Map.of(MESSAGE, OTP_SENT + email));
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPVerificationRequest otpVerificationRequest) {
        boolean isVerified = otpService.verifyOTP(otpVerificationRequest.getEmail(), otpVerificationRequest.getOtp());
        return isVerified
                ? ResponseEntity.ok(Map.of(MESSAGE, OTP_VERIFIED))
                : ResponseEntity.status(400).body(Map.of(MESSAGE, INVALID_OTP));
    }

    @GetMapping("/getotpfordoc/{email}")
    public ResponseEntity<?> getOtpfordoc(@PathVariable String email) {
        otpService.generateAndSendOTP(email);
        return ResponseEntity.ok(Map.of(MESSAGE, OTP_SENT + email));
    }

    @PostMapping("/verifyotpfordoc")
    public ResponseEntity<?> verifyotpdoc(@RequestParam Integer userId, @RequestParam String otp, MultipartFile aadharImage) {
        User user = userRepository.findById(userId).orElseThrow();
        boolean isVerified = otpService.aadharVerifyOTP(user.getEmail(), otp, aadharImage);
        return isVerified
                ? ResponseEntity.ok(Map.of(MESSAGE, OTP_VERIFIED))
                : ResponseEntity.status(400).body(Map.of(MESSAGE, INVALID_OTP));
    }

    @PostMapping("/verifyotpforpan")
    public ResponseEntity<?> verifypanotp(@RequestParam Integer userId, @RequestParam String otp, MultipartFile panImage) {
        User user = userRepository.findById(userId).orElseThrow();
        boolean isVerified = otpService.verifyPanOtp(user.getEmail(), otp, panImage);
        return isVerified
                ? ResponseEntity.ok(Map.of(MESSAGE, OTP_VERIFIED))
                : ResponseEntity.status(400).body(Map.of(MESSAGE, INVALID_OTP));
    }
}
