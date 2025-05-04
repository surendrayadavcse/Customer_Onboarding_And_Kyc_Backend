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
    KycDocumentService kycService;
    private static final Random RANDOM = new Random(); // ✅ Reused Random instance
    // Method to generate and send OTP
    public void generateAndSendOTP(String email) {
        // Generate a random OTP
    	String otp = String.valueOf(RANDOM.nextInt(9000) + 1000); // ✅ Use static instance

        // Store OTP in Redis with a -second expiry
        redisTemplate.opsForValue().set(email, otp, 300, TimeUnit.SECONDS);
        System.out.println(otp);
        // Send OTP to the user via email
        sendEmail(email, otp);
    }

    // Method to send email with OTP
    private void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("OTP Verification");
        message.setText("Your OTP is " + otp);
        mailSender.send(message);
    }

    public boolean verifyOTP(String email, String otp) {
    	String storedOtp = redisTemplate.opsForValue().get(email);

        // Check if OTP matches and is still valid (exists in Redis)
        return storedOtp != null && storedOtp.equals(otp);
    }
    // Method to verify OTP from Redis
    public boolean aadharVerifyOTP(String email, String otp, MultipartFile aadharImage) {
        String storedOtp = redisTemplate.opsForValue().get(email);
        System.out.println("Stored OTP: " + storedOtp + ", Provided OTP: " + otp);

        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(email); // Optional: Invalidate OTP after success
            kycService.saveVerifiedAadhar(email, aadharImage);
            return true;
        }
        return false;
    }

    
    public boolean verifyPanOtp(String email, String otp, MultipartFile panImage) {
    	String storedOtp = redisTemplate.opsForValue().get(email);
        System.out.println("Stored OTP: " + storedOtp + ", Provided OTP: " + otp);

        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(email); // Optional: Invalidate OTP after success
            kycService.saveVerifiedPan(email, panImage);
            return true;
        }
        return false;
    }


}
