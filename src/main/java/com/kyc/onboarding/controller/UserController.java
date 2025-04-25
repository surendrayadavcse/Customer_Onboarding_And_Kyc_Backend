package com.kyc.onboarding.controller;

import com.kyc.onboarding.dto.CustomerDTO;
import com.kyc.onboarding.dto.UserProfileResponseDTO;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String token = userService.loginUser(loginRequest.get("email"), loginRequest.get("password"));

        User user = userService.userRepository.findByEmail(loginRequest.get("email")).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "token", token,
                "role", user.getRole(),
                "kycstatus", user.getKycStatus()
        ));
    }

    @PatchMapping("/uploadbasicdetails")
    public ResponseEntity<String> updateUserDetails(@RequestBody User user) {
        userService.updateUserDetails(user);
        return ResponseEntity.ok("User details updated successfully.");
    }

    @GetMapping("/getallcustomers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(userService.getAllCustomers());
    }

    @GetMapping("/kycstatistics")
    public Map<String, Long> getKycStatistics() {
        return userService.getKycStatistics();
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable int userId, HttpServletRequest request) {
        UserProfileResponseDTO userProfile = userService.getUserProfile(userId);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";

        userProfile.setAadharImage(prependBaseUrlIfNeeded(userProfile.getAadharImage(), baseUrl));
        userProfile.setPanImage(prependBaseUrlIfNeeded(userProfile.getPanImage(), baseUrl));
        userProfile.setSelfieImage(prependBaseUrlIfNeeded(userProfile.getSelfieImage(), baseUrl));

        return ResponseEntity.ok(userProfile);
    }

    private String prependBaseUrlIfNeeded(String imagePath, String baseUrl) {
        if (imagePath != null && !imagePath.startsWith("http")) {
            return (baseUrl + imagePath).replace("\\", "/");
        }
        return imagePath;
    }
}