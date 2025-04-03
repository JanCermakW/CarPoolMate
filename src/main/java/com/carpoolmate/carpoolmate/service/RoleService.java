package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.RoleType;

import java.util.List;

public interface RoleService {
    List<Role> getRolesById(List<Long> id);

    List<Role> getAllRoles();

    boolean existsByName(RoleType name);

    void saveRole(Role role);
}
