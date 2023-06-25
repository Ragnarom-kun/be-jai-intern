package com.intern.techtest.repository;

import java.util.Optional;

import com.intern.techtest.model.Role;
import com.intern.techtest.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
