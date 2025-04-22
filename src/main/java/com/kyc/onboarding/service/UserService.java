package com.kyc.onboarding.service;

import com.kyc.onboarding.dto.CustomerDTO;
import com.kyc.onboarding.dto.UserProfileResponseDTO;
import com.kyc.onboarding.model.KycDocument;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.KycDocumentsRepository;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userRepository.existsByMobile(user.getMobile())) {
            throw new IllegalArgumentException("Mobile number is already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CUSTOMER");
        user.setKycStatus("PENDING");
        user.setDob(null);
        user.setAddress(null);

        userRepository.save(user);
    }


    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return jwtUtil.generateToken(email, userOpt.get().getRole());
        }
        return null;
    }
    public List<CustomerDTO> getAllCustomers() {
        return userRepository.findAllCustomers();
    }



    public void checkAndUpdateKycStatus(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        boolean updated = false;

        // Step 1: Address + DOB updated
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

        if (aadharSuccess && panSuccess && !"STEP 2 COMPLETED".equals(user.getKycStatus()) && !"KYC COMPLETED".equals(user.getKycStatus())) {
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
   // Fetch the existing user by ID
    User existingUser = userRepository.findById(user.getId())
           .orElseThrow(() -> new RuntimeException("User not found"));

    boolean updated = false; // Flag to check if fields were updated

    // Only update provided fields (dob and address)
   if (user.getDob() != null) {
        existingUser.setDob(user.getDob());
       updated = true; // Set to true if dob is updated
    }

  if (user.getAddress() != null) {
      existingUser.setAddress(user.getAddress());
       updated = true; // Set to true if address is updated
  }
//
//    // If either dob or address was updated, we update KYC status
    if (updated) {
//        // Save the updated user entity without overwriting other fields
        userRepository.save(existingUser);
//
//        // Now, check and update the KYC status based on the updated details
        checkAndUpdateKycStatus(existingUser);
    }
}
  
  public Map<String, Long> getKycStatistics() {
      long totalUsers = userRepository.count();
      long step1CompletedUsers = userRepository.countByKycStatus("STEP 1 COMPLETED");
      long step2CompletedUsers = userRepository.countByKycStatus("STEP 2 COMPLETED");
      long kycCompletedUsers = userRepository.countByKycStatus("KYC COMPLETED");

      // Pending users: All users not having "KYC COMPLETED"
      long pendingUsers = totalUsers - kycCompletedUsers;

      Map<String, Long> stats = new HashMap();
      stats.put("totalUsers", totalUsers);
      stats.put("step1CompletedUsers", step1CompletedUsers);
      stats.put("step2CompletedUsers", step2CompletedUsers);
      stats.put("kycCompletedUsers", kycCompletedUsers);
      stats.put("pendingUsers", pendingUsers); // Pending: those who are NOT "KYC COMPLETED"

      return stats;
  }
  public UserProfileResponseDTO getUserProfile(int userId) {
      User user = userRepository.findById(userId)
              .orElseThrow();

    
	KycDocument kycDocument = kycDocumentRepository.findByUserId(userId);

      UserProfileResponseDTO dto = new UserProfileResponseDTO();

      dto.setId(user.getId());
      dto.setFullName(user.getFullName());
      dto.setMobile(user.getMobile());
      dto.setEmail(user.getEmail());
      dto.setRole(user.getRole());
      dto.setKycStatus(user.getKycStatus());
      dto.setDob(user.getDob() != null ? user.getDob().toString() : null);
      dto.setAddress(user.getAddress());

      if (kycDocument != null) {
          dto.setAadharNumber(kycDocument.getAadharNumber());
          dto.setAadharImage(kycDocument.getAadharImage());
          dto.setPanNumber(kycDocument.getPanNumber());
          dto.setPanImage(kycDocument.getPanImage());
          dto.setSelfieImage(kycDocument.getSelfieImage());
      }

      return dto;
  }
  
  


}
