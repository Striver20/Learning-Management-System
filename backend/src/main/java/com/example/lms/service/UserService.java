package com.example.lms.service;

import com.example.lms.dto.request.RegisterRequest;
import com.example.lms.entity.Role;
import com.example.lms.entity.RoleName;
import com.example.lms.entity.User;
import com.example.lms.exception.ConflictException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.repository.RoleRepository;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        log.info("🔵 Registration request received for email: {}", request.getEmail());
        log.info("🔵 Requested roles: {}", request.getRoles());
        
        // ✅ Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("❌ Email already registered: {}", request.getEmail());
            throw new ConflictException("Email already registered: " + request.getEmail());
        }

        // ✅ Build User entity from DTO
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .build();

        // ✅ Assign roles
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            log.info("🔵 No roles specified, defaulting to ROLE_STUDENT");
            Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new ResourceNotFoundException("ROLE_STUDENT not found in database"));
            user.setRoles(Set.of(studentRole));
        } else {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                log.info("🔵 Looking for role: {}", roleName);
                try {
                    RoleName roleNameEnum = RoleName.valueOf(roleName);
                    log.info("🔵 Parsed role enum: {}", roleNameEnum);
                    
                    Role role = roleRepository.findByRoleName(roleNameEnum)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found in DB: " + roleName));
                    log.info("✅ Found role in DB: {} (ID: {})", role.getRoleName(), role.getId());
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    log.error("❌ Invalid role name: {}. Valid values: ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN", roleName);
                    throw new ResourceNotFoundException("Invalid role name: " + roleName);
                }
            }
            user.setRoles(roles);
            log.info("✅ Assigned {} role(s) to user", roles.size());
        }

        User savedUser = userRepository.save(user);
        log.info("✅ User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }
    public User save(User user) { return userRepository.save(user); }
    public void deleteById(Long id) { userRepository.deleteById(id); }

}
