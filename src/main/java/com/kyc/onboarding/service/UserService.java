package com.kyc.onboarding.service;

import com.kyc.onboarding.dto.CustomerDTO;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
	public UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

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

    boolean updated = false; // Flag to track if the status was updated
   
    // Step 1: Address + DOB updated
    if (user.getAddress() != null && user.getDob() != null && !user.getKycStatus().equals("STEP 1 COMPLETED")) {
        user.setKycStatus("STEP 1 COMPLETED");
        updated = true;
    }
//    if (user.getKycDocument() == null) {
//        // No KYC document uploaded yet
//        userRepository.save(user);
//       return;
//    }

  

    // Step 2: Aadhaar and PAN verified
    if (Boolean.TRUE.equals(user.getKycDocument().getAadhaarVerified()) 
        && Boolean.TRUE.equals(user.getKycDocument().getPanVerified()) 
        && !user.getKycStatus().equals("STEP 2 COMPLETED")) {
        user.setKycStatus("STEP 2 COMPLETED");
        updated = true;
    }

    // Final Step: Selfie uploaded
    if (user.getKycDocument().getSelfieImage() != null && !user.getKycStatus().equals("KYC COMPLETED")) {
        user.setKycStatus("KYC COMPLETED");
        updated = true;
    }

    // Only save if there was an update
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

    // If either dob or address was updated, we update KYC status
    if (updated) {
        // Save the updated user entity without overwriting other fields
        userRepository.save(existingUser);

        // Now, check and update the KYC status based on the updated details
        checkAndUpdateKycStatus(existingUser);
    }
}


}
