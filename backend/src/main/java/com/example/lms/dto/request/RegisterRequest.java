package com.example.lms.dto.request;

import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String bio;
    private String avatarUrl;
    private Set<String> roles; // e.g., ["ROLE_STUDENT", "ROLE_TEACHER"]
}
