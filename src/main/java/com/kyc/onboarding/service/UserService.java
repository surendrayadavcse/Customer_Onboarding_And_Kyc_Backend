package com.kyc.onboarding.service;

import com.kyc.onboarding.constants.KycStatus;
import com.kyc.onboarding.dto.AddressDobDTO;
import com.kyc.onboarding.dto.CustomerDTO;
import com.kyc.onboarding.dto.UserProfileResponseDTO;
import com.kyc.onboarding.exception.*;
import com.kyc.onboarding.model.KycDocument;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.KycDocumentsRepository;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.security.EncryptionUtil;
import com.kyc.onboarding.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private VerificationLogService verificationLogService;
    @Autowired
    private KycDocumentsRepository kycDocumentRepository;

    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        if (userRepository.existsByMobile(user.getMobile())) {
            throw new UserAlreadyExistsException("Mobile number is already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CUSTOMER");
        user.setKycStatus("PENDING");
        user.setDob(null);
        user.setAddress(null);
        user.setRegistereddate(LocalDateTime.now());

        userRepository.save(user);
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        return jwtUtil.generateToken(email, user.getRole());
    }

    public List<CustomerDTO> getAllCustomers() {
        return userRepository.findAllCustomers();
    }

public void checkAndUpdateKycStatus(User user) {
    if (user == null) {
        throw new IllegalArgumentException("User cannot be null");
    }

    boolean updated = false;

    if (user.getAddress() != null && user.getDob() != null && !KycStatus.STEP1_COMPLETED.equals(user.getKycStatus())) {
        user.setKycStatus(KycStatus.STEP1_COMPLETED);
        updated = true;
    }

    if (user.getKycDocument() == null) {
        userRepository.save(user);
        return;
    }

    boolean aadharSuccess = verificationLogService.hasSuccessfulVerification(user.getId(), "AADHAR");
    boolean panSuccess = verificationLogService.hasSuccessfulVerification(user.getId(), "PAN");
    boolean selfieSuccess = verificationLogService.hasSuccessfulVerification(user.getId(), "SELFIE");

    if (aadharSuccess && panSuccess &&
            user.getKycDocument().getAadharNumber() != null &&
            user.getKycDocument().getPanNumber() != null &&
            !KycStatus.STEP2_COMPLETED.equals(user.getKycStatus()) &&
            !KycStatus.KYC_COMPLETED.equals(user.getKycStatus())) {

        user.setKycStatus(KycStatus.STEP2_COMPLETED);
        updated = true;
    }

    if (selfieSuccess && !KycStatus.KYC_COMPLETED.equals(user.getKycStatus())) {
        user.setKycStatus(KycStatus.KYC_COMPLETED);
        updated = true;
    }

    if (updated) {
        userRepository.save(user);
    }
}

    public void updateUserDetails(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean updated = false;

        if (user.getDob() != null) {
            existingUser.setDob(user.getDob());
            updated = true;
        }

        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
            updated = true;
        }

        if (updated) {
            userRepository.save(existingUser);
            checkAndUpdateKycStatus(existingUser);
        }
    }

   public Map<String, Long> getKycStatistics() {
    long totalCustomers = userRepository.countByRole("CUSTOMER");
    long step1CompletedCustomers = userRepository.countByRoleAndKycStatus("CUSTOMER", "STEP 1 COMPLETED");
    long step2CompletedCustomers = userRepository.countByRoleAndKycStatus("CUSTOMER", "STEP 2 COMPLETED");
    long kycCompletedCustomers = userRepository.countByRoleAndKycStatus("CUSTOMER", "KYC COMPLETED");
    long pendingCustomers = totalCustomers - kycCompletedCustomers;
    long newCustomerRegistrations = userRepository.countTodayRegisteredUsers();

    Map<String, Long> stats = new HashMap<>();
    stats.put("totalUsers", totalCustomers);
    stats.put("step1CompletedCustomers", step1CompletedCustomers);
    stats.put("step2CompletedCustomers", step2CompletedCustomers);
    stats.put("kycCompletedUsers", kycCompletedCustomers);
    stats.put("pendingUsers", pendingCustomers);
    stats.put("newRegistrations", newCustomerRegistrations);

    return stats;
}


    public UserProfileResponseDTO getUserProfile(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<KycDocument> optionalKycDocument = kycDocumentRepository.findByUserId(userId);

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setMobile(user.getMobile());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setKycStatus(user.getKycStatus());
        dto.setDob(user.getDob() != null ? user.getDob().toString() : null);
        dto.setAddress(user.getAddress());

        optionalKycDocument.ifPresent(kycDocument -> {
            dto.setAadharNumber(EncryptionUtil.decrypt(kycDocument.getAadharNumber()));
            dto.setAadharImage(kycDocument.getAadharImage());
            dto.setPanNumber(EncryptionUtil.decrypt(kycDocument.getPanNumber()));
            dto.setPanImage(kycDocument.getPanImage());
            dto.setSelfieImage(kycDocument.getSelfieImage());
        });

        return dto;
    }

    public String getKycStatusByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getKycStatus();
    }

	
    public String getemailbyid(Integer userId) {
		// TODO Auto-generated method stub
		  User user = userRepository.findById(userId)
			        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
			    return user.getEmail();
	}

	public AddressDobDTO getAddressAndDob(int userId) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    return new AddressDobDTO(user.getAddress(), user.getDob());
	}


}
