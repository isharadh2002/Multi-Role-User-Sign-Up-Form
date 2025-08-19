package com.internship.user_registration.repository;

import com.internship.user_registration.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role exists by name
     * @param name the role name
     * @return true if role exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find roles by names
     * @param names set of role names
     * @return set of roles matching the names
     */
    Set<Role> findByNameIn(Set<String> names);

    /**
     * Get all available role names
     * @return set of all role names
     */
    @Query("SELECT r.name FROM Role r")
    Set<String> findAllRoleNames();
}