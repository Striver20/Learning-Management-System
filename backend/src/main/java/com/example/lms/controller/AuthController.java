package com.example.lms.controller;

import com.example.lms.dto.UserDTO;
import com.example.lms.dto.request.LoginRequest;
import com.example.lms.dto.request.RegisterRequest;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.security.CustomUserDetails;
import com.example.lms.security.JwtUtil;
import com.example.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        User saved = userService.registerUser(request); // now takes DTO
        return ResponseEntity.ok(EntityMapper.toUserDTO(saved));
    }

    // ✅ Login with JSON body (email + password)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // 🔥 Pass User (or wrap in CustomUserDetails) to JwtUtil
                String token = jwtUtil.generateToken(new CustomUserDetails(user));
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(
                userService.findAllUsers().stream()
                        .map(EntityMapper::toUserDTO)
                        .toList()
        );
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(EntityMapper.toUserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
