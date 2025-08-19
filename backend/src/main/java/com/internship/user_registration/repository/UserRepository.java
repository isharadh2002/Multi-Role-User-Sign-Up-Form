package com.internship.user_registration.repository;

import com.internship.user_registration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find user by phone number
     * @param phoneNumber the phone number to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check if user exists by phone number
     * @param phoneNumber the phone number to check
     * @return true if user exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Find users by country
     * @param country the country code
     * @return list of users from the specified country
     */
    @Query("SELECT u FROM User u WHERE u.country = :country")
    java.util.List<User> findByCountry(@Param("country") String country);

    /**
     * Count users by role name
     * @param roleName the role name
     * @return count of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
}