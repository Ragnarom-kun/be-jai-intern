package com.intern.techtest.service;

import com.intern.techtest.model.Role;
import com.intern.techtest.model.ERole;
import com.intern.techtest.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RolesServiceImpl implements RolesService {
    @Autowired
    RoleRepository roleRepository;

    public Role findByName(ERole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    @Override
    public Set<Role> assignRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = findByName(ERole.BASIC_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN" -> {
                        Role adminRole = findByName(ERole.ADMIN);
                        roles.add(adminRole);
                    }
                    case "PREMIUM" -> {
                        Role userRole = findByName(ERole.PREMIUM_USER);
                        roles.add(userRole);
                    }
                    default -> {
                        Role userRole = findByName(ERole.BASIC_USER);
                        roles.add(userRole);
                    }
                }
            });
        }

        return roles;
    }
}