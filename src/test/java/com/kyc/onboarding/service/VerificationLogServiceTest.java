package com.kyc.onboarding.service;

import com.kyc.onboarding.model.User;
import com.kyc.onboarding.model.VerificationLog;
import com.kyc.onboarding.repository.VerificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationLogServiceTest {

    @InjectMocks
    private VerificationLogService verificationLogService;

    @Mock
    private VerificationLogRepository verificationLogRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogAttempt_SavesLogSuccessfully() {
        User user = new User();
        user.setId(1);

        verificationLogService.logAttempt(user, "AADHAR", "SUCCESS", "Verified successfully");

        // We can't verify log content directly, but we can verify save() was called once.
        verify(verificationLogRepository, times(1)).save(any(VerificationLog.class));
    }

    @Test
    void testGetLogsByUser_ReturnsLogList() {
        VerificationLog log = new VerificationLog();
        log.setVerificationType("PAN");

        when(verificationLogRepository.findByUserId(1)).thenReturn(List.of(log));

        List<VerificationLog> result = verificationLogService.getLogsByUser(1);

        assertEquals(1, result.size());
        assertEquals("PAN", result.get(0).getVerificationType());
    }

    @Test
    void testGetLogsByUser_ReturnsEmptyListWhenNull() {
        when(verificationLogRepository.findByUserId(1)).thenReturn(null);

        List<VerificationLog> result = verificationLogService.getLogsByUser(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void testHasSuccessfulVerification_ReturnsTrue() {
        when(verificationLogRepository.existsByUserIdAndVerificationTypeIgnoreCaseAndStatusIgnoreCase(1, "AADHAR", "SUCCESS"))
                .thenReturn(true);

        boolean result = verificationLogService.hasSuccessfulVerification(1, "AADHAR");

        assertTrue(result);
    }

    @Test
    void testIsDocumentAlreadyVerified_ReturnsFalse() {
        when(verificationLogRepository.existsByUserIdAndVerificationTypeIgnoreCaseAndStatusIgnoreCase(1, "PAN", "SUCCESS"))
                .thenReturn(false);

        boolean result = verificationLogService.isDocumentAlreadyVerified(1, "PAN");

        assertFalse(result);
    }
}
