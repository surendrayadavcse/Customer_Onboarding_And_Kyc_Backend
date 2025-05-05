package com.kyc.onboarding.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OTPServiceTest {

    @InjectMocks
    private OTPService otpService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGenerateAndSendOTP() {
        String email = "user@example.com";

        // Act
        otpService.generateAndSendOTP(email,"aadhar");

        // Assert Redis set with expiry
        verify(valueOperations, times(1)).set(eq(email), anyString(), eq(300L), eq(TimeUnit.SECONDS));

        // Assert email sent
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testVerifyOTP_Success() {
        String email = "user@example.com";
        String otp = "1234";

        when(valueOperations.get(email)).thenReturn("1234");

        boolean result = otpService.verifyOTP(email, otp);

        assertTrue(result);
    }

    @Test
    void testVerifyOTP_Failure() {
        String email = "user@example.com";
        String otp = "1234";

        when(valueOperations.get(email)).thenReturn("5678");

        boolean result = otpService.verifyOTP(email, otp);

        assertFalse(result);
    }

    @Test
    void testVerifyOTP_Expired() {
        String email = "user@example.com";
        String otp = "1234";

        when(valueOperations.get(email)).thenReturn(null);  // Simulates expired or missing

        boolean result = otpService.verifyOTP(email, otp);

        assertFalse(result);
    }
}
