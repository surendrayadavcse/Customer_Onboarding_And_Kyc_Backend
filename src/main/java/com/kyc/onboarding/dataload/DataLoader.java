package com.kyc.onboarding.dataload;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.UserRepository;


@Component
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    public DataLoader(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepo.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setFullName("Mark");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));  // Set and hash a real password
            admin.setRole("ADMIN");
            admin.setKycStatus("PENDING");
            userRepo.save(admin);
        }

 
        
    }
}
