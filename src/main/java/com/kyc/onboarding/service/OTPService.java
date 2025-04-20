package com.kyc.onboarding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    // Method to generate and send OTP
    public void generateAndSendOTP(String email) {
        // Generate a random OTP
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        // Store OTP in Redis with a -second expiry
        redisTemplate.opsForValue().set(email, otp, 300, TimeUnit.SECONDS);

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

    // Method to verify OTP from Redis
    public boolean verifyOTP(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(email);

        // Check if OTP matches and is still valid (exists in Redis)
        return storedOtp != null && storedOtp.equals(otp);
    }
}
