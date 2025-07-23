package com.skillassessment.platform.repository;

import com.skillassessment.platform.entity.Role;
import com.skillassessment.platform.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Role} entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     * This is a crucial method for assigning roles to users based on the RoleName enum.
     *
     * @param name The RoleName enum constant (e.g., ROLE_USER, ROLE_ADMIN).
     * @return An Optional containing the found Role, or an empty Optional if no role with that name exists.
     */
    Optional<Role> findByName(RoleName name);
}