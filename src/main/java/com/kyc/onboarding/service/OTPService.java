package com.kyc.onboarding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private KycDocumentService kycService;

    private static final Random RANDOM = new Random();

    // Context-aware OTP generator
    public void generateAndSendOTP(String email, String context) {
        String otp = String.valueOf(RANDOM.nextInt(9000) + 1000);
        redisTemplate.opsForValue().set(email, otp, 300, TimeUnit.SECONDS);
        System.out.println("Generated OTP for " + context + ": " + otp);
        sendEmail(email, otp, context);
    }

    private void sendEmail(String to, String otp, String context) {
        String subject;
        String body;

        switch (context.toLowerCase()) {
            case "aadhar":
                subject = "OTP Verification for Aadhaar Upload - HexaEdge Ltd";
                body = "Hello,\n\nPlease use the following OTP to verify your Aadhaar document upload:\n\n"
                        + "Your OTP: " + otp
                        + "\n\nThis OTP is valid for 5 minutes. If you did not request this, please ignore this email.\n\n"
                        + "Regards,\nHexaEdge Ltd Team";
                break;
            case "pan":
                subject = "OTP Verification for PAN Upload - HexaEdge Ltd";
                body = "Hello,\n\nPlease use the following OTP to verify your PAN document upload:\n\n"
                        + "Your OTP: " + otp
                        + "\n\nThis OTP is valid for 5 minutes. If you did not request this, please ignore this email.\n\n"
                        + "Regards,\nHexaEdge Ltd Team";
                break;
            case "forgotpassword":
                subject = "Password Reset OTP - HexaEdge Ltd";
                body = "Hello,\n\nWe received a request to reset your password. Please use the OTP below to proceed:\n\n"
                        + "Your OTP: " + otp
                        + "\n\nThis OTP is valid for 5 minutes. If you did not request a password reset, please ignore this email.\n\n"
                        + "Regards,\nHexaEdge Ltd Support Team";
                break;
            default:
                subject = "OTP Verification for Your HexaEdge Ltd Registration";
                body = "Hello,\n\nThank you for registering with HexaEdge Ltd. Please use the following OTP to verify your account:\n\n"
                        + "Your OTP: " + otp
                        + "\n\nThis OTP is valid for 5 minutes. If you did not request this, please ignore this email.\n\n"
                        + "Best regards,\nHexaEdge Ltd Team\nCustomer Support";
                break;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public boolean verifyOTP(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }

    public boolean aadharVerifyOTP(String email, String otp, MultipartFile aadharImage) {
        if (verifyOTP(email, otp)) {
            redisTemplate.delete(email);
            kycService.saveVerifiedAadhar(email, aadharImage);
            return true;
        }
        return false;
    }

    public boolean verifyPanOtp(String email, String otp, MultipartFile panImage) {
        if (verifyOTP(email, otp)) {
            redisTemplate.delete(email);
            kycService.saveVerifiedPan(email, panImage);
            return true;
        }
        return false;
    }
}
