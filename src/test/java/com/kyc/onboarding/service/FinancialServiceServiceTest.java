package com.kyc.onboarding.service;

import com.kyc.onboarding.model.FinancialService;
import com.kyc.onboarding.repository.FinancialServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialServiceServiceTest {

    @InjectMocks
    private FinancialServiceService financialServiceService;

    @Mock
    private FinancialServiceRepository financialServiceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddServiceAndGetAllServices() {
        // ----------- Add Service Test ----------
        FinancialService newService = new FinancialService();
        newService.setServiceName("KYC Verification");
        newService.setServiceIconPath("/icons/kyc.png");

        when(financialServiceRepository.existsByServiceName("KYC Verification")).thenReturn(false);
        when(financialServiceRepository.save(newService)).thenReturn(newService);

        FinancialService addedService = financialServiceService.addService(newService);

        assertEquals("KYC Verification", addedService.getServiceName());
        verify(financialServiceRepository).save(newService);

        // ----------- Get All Services Test ----------
        when(financialServiceRepository.findAll()).thenReturn(List.of(newService));

        List<FinancialService> services = financialServiceService.getAllServices("http://localhost:8080");

        assertEquals(1, services.size());
        assertEquals("http://localhost:8080/icons/kyc.png", services.get(0).getServiceIconPath());
    }
}
