package com.kyc.onboarding.controller;

import com.kyc.onboarding.dto.CustomerDTO;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String token = userService.loginUser(loginRequest.get("email"), loginRequest.get("password"));
        
        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid Credentials"));
        }

        String role = "CUSTOMER";
        Optional<User> userOpt = userService.userRepository.findByEmail(loginRequest.get("email"));
        if (userOpt.isPresent()) {
            role = userOpt.get().getRole();
        }

        return ResponseEntity.ok(Map.of("token", token, "role", role));
    }
    
    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<CustomerDTO> customers = userService.getAllCustomers();

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No customers found");
            }

            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Something went wrong while fetching customers");
        }
    }

  @PatchMapping("/update-details")
    public ResponseEntity<String> updateUserDetails(@RequestBody User user) {
        try {
            // Call service method to update only the fields that are provided
            userService.updateUserDetails(user);
            return ResponseEntity.ok("User details updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update user details: " + e.getMessage());
        }
    }
  
  @GetMapping("/kyc-statistics")
  public Map<String, Long> getKycStatistics() {
      return userService.getKycStatistics();
  }
    

}
