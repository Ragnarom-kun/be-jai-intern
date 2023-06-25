package com.intern.techtest.service;

import com.intern.techtest.model.Role;

import java.util.Set;

public interface RolesService {
    Set<Role> assignRoles(Set<String> strRoles);
}
