package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleType name);

    boolean existsByName(RoleType name);
}
