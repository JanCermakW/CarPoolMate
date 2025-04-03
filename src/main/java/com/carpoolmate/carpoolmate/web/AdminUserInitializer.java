package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.RoleType;
import com.carpoolmate.carpoolmate.repository.RoleRepository;
import com.carpoolmate.carpoolmate.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.UserService;

import java.util.Arrays;
import java.util.Set;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExists(RoleType.ADMIN);
        createRoleIfNotExists(RoleType.USER);
        createRoleIfNotExists(RoleType.DRIVER);

        User user = userService.getUserByEmail("admin@example.com");
        if (user == null) {

            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@example.com");
            adminUser.setEnabled(true);
            adminUser.setPassword(passwordEncoder.encode("admin"));

            adminUser.setRole(roleRepository.findByName(RoleType.ADMIN));

            userService.saveUserStartup(adminUser);
        }
    }

    private void createRoleIfNotExists(RoleType roleName) {
        if (!roleService.existsByName(roleName)) {
            Role role = new Role(roleName);
            roleService.saveRole(role);
        }
    }
}
