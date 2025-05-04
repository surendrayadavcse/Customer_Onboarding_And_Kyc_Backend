package com.kyc.onboarding.repository;

import com.kyc.onboarding.dto.CustomerDTO;
import com.kyc.onboarding.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    boolean existsById(int id);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    @Query("SELECT new com.kyc.onboarding.dto.CustomerDTO(u.fullName, u.email, u.kycStatus, u.registereddate) FROM User u WHERE u.role = 'CUSTOMER'")
    List<CustomerDTO> findAllCustomers();

    @Query(value = "SELECT COUNT(*) FROM users WHERE DATE(registereddate) = CURRENT_DATE AND role = 'CUSTOMER'", nativeQuery = true)
    long countTodayRegisteredUsers();

    long countByKycStatus(String kycStatus);

    // ✅ New methods for filtering by role and KYC status
    long countByRole(String role);

    long countByRoleAndKycStatus(String role, String kycStatus);
}
