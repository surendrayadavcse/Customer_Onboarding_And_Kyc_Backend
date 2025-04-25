package com.kyc.onboarding.service;

import com.kyc.onboarding.exception.ServiceAlreadyExistsException;
import com.kyc.onboarding.exception.ServiceNotFoundException;
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
        if (financialServiceRepository.existsByServiceName(service.getServiceName())) {
            throw new ServiceAlreadyExistsException("Service with this name already exists.");
        }
        return financialServiceRepository.save(service);
    }

    public List<FinancialService> getAllServices(String baseUrl) {
        List<FinancialService> services = financialServiceRepository.findAll();
        for (FinancialService service : services) {
            if (service.getServiceIconPath() != null && !service.getServiceIconPath().startsWith("http")) {
                service.setServiceIconPath(baseUrl + service.getServiceIconPath());
            }
        }
        return services;
    }

    public void deleteServiceById(Integer id) {
        if (!financialServiceRepository.existsById(id)) {
            throw new ServiceNotFoundException("Service with ID " + id + " does not exist.");
        }
        financialServiceRepository.deleteById(id);
    }
}
