package com.example.lms.controller;

import com.example.lms.dto.UserDTO;
import com.example.lms.dto.request.LoginRequest;
import com.example.lms.dto.request.RegisterRequest;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.security.CustomUserDetails;
import com.example.lms.security.JwtUtil;
import com.example.lms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Register a new user", description = "Create a new user account with role (ADMIN, TEACHER, or STUDENT)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        User saved = userService.registerUser(request); // now takes DTO
        return ResponseEntity.ok(EntityMapper.toUserDTO(saved));
    }

    @Operation(summary = "User login", description = "Authenticate user and receive JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
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


    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)", 
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(
                userService.findAllUsers().stream()
                        .map(EntityMapper::toUserDTO)
                        .toList()
        );
    }

    @Operation(summary = "Get user by email", description = "Retrieve user details by email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(EntityMapper.toUserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
