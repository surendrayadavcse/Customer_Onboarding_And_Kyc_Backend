package com.kyc.onboarding.service;

import com.kyc.onboarding.exception.UserNotFoundException;
import com.kyc.onboarding.model.Consent;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.ConsentRepository;
import com.kyc.onboarding.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsentServiceTest {

    @InjectMocks
    private ConsentService consentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsentRepository consentRepository;

    private User user;
    private Consent consent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setFullName("John");

        consent = new Consent();
        consent.setUser(user);
        consent.setConsentGiven(false);
    }

    @Test
    void testSubmitConsent_NewConsent() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consentRepository.findByUserId(1)).thenReturn(null);
        when(consentRepository.save(any(Consent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Consent result = consentService.submitConsent(1, true);

        assertNotNull(result);
        assertEquals(true, result.isConsentGiven());
        assertEquals(user, result.getUser());
        verify(consentRepository, times(1)).save(any(Consent.class));
    }

    @Test
    void testSubmitConsent_UpdateExistingConsent() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consentRepository.findByUserId(1)).thenReturn(consent);
        when(consentRepository.save(any(Consent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Consent result = consentService.submitConsent(1, true);

        assertTrue(result.isConsentGiven());
        assertEquals(user, result.getUser());
        verify(consentRepository).save(consent);
    }

    @Test
    void testSubmitConsent_UserNotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> consentService.submitConsent(2, true));
    }

    @Test
    void testGetConsentStatus_ValidConsent() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        consent.setConsentGiven(true);
        when(consentRepository.findByUserId(1)).thenReturn(consent);

        Boolean result = consentService.getConsentStatus(2);  //org 1

        assertTrue(result);
    }

    @Test
    void testGetConsentStatus_UserNotFound() {
        when(userRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> consentService.getConsentStatus(3));
    }

    @Test
    void testGetConsentStatus_ConsentNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consentRepository.findByUserId(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> consentService.getConsentStatus(1));
    }
}
