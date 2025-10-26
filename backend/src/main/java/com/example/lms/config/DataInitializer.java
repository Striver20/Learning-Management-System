package com.example.lms.config;

import com.example.lms.entity.Role;
import com.example.lms.entity.RoleName;
import com.example.lms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Database initializer to seed default roles
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initializeRoles();
    }

    private void initializeRoles() {
        log.info("Checking and initializing roles...");
        
        // Create ROLE_STUDENT if it doesn't exist
        if (roleRepository.findByRoleName(RoleName.ROLE_STUDENT).isEmpty()) {
            Role studentRole = new Role();
            studentRole.setRoleName(RoleName.ROLE_STUDENT);
            roleRepository.save(studentRole);
            log.info("✅ Created ROLE_STUDENT");
        }

        // Create ROLE_TEACHER if it doesn't exist
        if (roleRepository.findByRoleName(RoleName.ROLE_TEACHER).isEmpty()) {
            Role teacherRole = new Role();
            teacherRole.setRoleName(RoleName.ROLE_TEACHER);
            roleRepository.save(teacherRole);
            log.info("✅ Created ROLE_TEACHER");
        }

        // Create ROLE_ADMIN if it doesn't exist
        if (roleRepository.findByRoleName(RoleName.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setRoleName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
            log.info("✅ Created ROLE_ADMIN");
        }

        log.info("✅ Role initialization complete. Total roles: {}", roleRepository.count());
    }
}
