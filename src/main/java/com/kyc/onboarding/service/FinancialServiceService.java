package com.kyc.onboarding.service;

import com.kyc.onboarding.model.FinancialService;
import com.kyc.onboarding.repository.FinancialServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialServiceService {

    @Autowired
    private FinancialServiceRepository financialServiceRepository;

    public FinancialService addService(FinancialService service) {
        // Check if service with same name exists
        if (financialServiceRepository.existsByServiceName(service.getServiceName())) {
            throw new IllegalArgumentException("Service with this name already exists.");
        }
        return financialServiceRepository.save(service);
    }

    public List<FinancialService> getAllServices() {
        return financialServiceRepository.findAll();
    }
    public void deleteServiceById(Integer id) {
        if (!financialServiceRepository.existsById(id)) {
            throw new IllegalArgumentException("Service with ID " + id + " does not exist.");
        }
        financialServiceRepository.deleteById(id);
    }

}
