package com.kyc.onboarding.service;

import com.kyc.onboarding.dto.UserProfileResponseDTO;
import com.kyc.onboarding.model.KycDocument;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.KycDocumentsRepository;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KycDocumentsRepository kycDocumentsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private VerificationLogService verificationLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setMobile("1234567890");
        user.setPassword("password");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByMobile(user.getMobile())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPwd");

        userService.registerUser(user);

        assertEquals("encodedPwd", user.getPassword());
        assertEquals("CUSTOMER", user.getRole());
        assertEquals("PENDING", user.getKycStatus());
        verify(userRepository).save(user);
    }

    @Test
    void testLoginUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPwd");
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPwd")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "CUSTOMER")).thenReturn("mockJwt");

        String token = userService.loginUser("test@example.com", "password");

        assertEquals("mockJwt", token);
    }

    @Test
    void testGetUserProfile_Success() {
        User user = new User();
        user.setId(1);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setRole("CUSTOMER");
        user.setMobile("1234567890");
        user.setKycStatus("KYC COMPLETED");
        user.setDob(LocalDate.of(1990, 1, 1));
        user.setAddress("123 Street");

        KycDocument doc = new KycDocument();
        doc.setAadharNumber("123412341234");
        doc.setPanNumber("ABCDE1234F");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(kycDocumentsRepository.findByUserId(1)).thenReturn(doc);

        UserProfileResponseDTO dto = userService.getUserProfile(1);

        assertEquals("John Doe", dto.getFullName());
        assertEquals("123412341234", dto.getAadharNumber());
        assertEquals("ABCDE1234F", dto.getPanNumber());
    }

    @Test
    void testGetKycStatusByUserId_Success() {
        User user = new User();
        user.setId(2);
        user.setKycStatus("STEP 2 COMPLETED");

        when(userRepository.findById(2)).thenReturn(Optional.of(user));

        String status = userService.getKycStatusByUserId(2);

        assertEquals("STEP 2 COMPLETED", status);
    }
}
