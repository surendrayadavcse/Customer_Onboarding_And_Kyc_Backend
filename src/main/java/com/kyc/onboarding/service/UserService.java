package com.kyc.onboarding.service;

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

        if (user.getAddress() != null && user.getDob() != null && !"STEP 1 COMPLETED".equals(user.getKycStatus())) {
            user.setKycStatus("STEP 1 COMPLETED");
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
        	    !"STEP 2 COMPLETED".equals(user.getKycStatus()) &&
        	    !"KYC COMPLETED".equals(user.getKycStatus())) {
        	    
        	    user.setKycStatus("STEP 2 COMPLETED");
        	    updated = true;
        	}


        if (selfieSuccess && !"KYC COMPLETED".equals(user.getKycStatus())) {
            user.setKycStatus("KYC COMPLETED");
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
        long totalUsers = userRepository.count();
        long step1CompletedUsers = userRepository.countByKycStatus("STEP 1 COMPLETED");
        long step2CompletedUsers = userRepository.countByKycStatus("STEP 2 COMPLETED");
        long kycCompletedUsers = userRepository.countByKycStatus("KYC COMPLETED");
        long pendingUsers = totalUsers - kycCompletedUsers;
        long newRegistrations = userRepository.countTodayRegisteredUsers();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("step1CompletedUsers", step1CompletedUsers);
        stats.put("step2CompletedUsers", step2CompletedUsers);
        stats.put("kycCompletedUsers", kycCompletedUsers);
        stats.put("pendingUsers", pendingUsers);
        stats.put("newRegistrations", newRegistrations); // ✅ added here

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
		Optional<User> userOpt=userRepository.findById(userId);
		
		User user = userOpt.get();
		return user.getEmail();
	}

	public AddressDobDTO getAddressAndDob(int userId) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    return new AddressDobDTO(user.getAddress(), user.getDob());
	}


}
