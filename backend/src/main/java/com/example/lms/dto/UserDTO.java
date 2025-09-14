package com.example.lms.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String bio;
    private String avatarUrl;
    private Set<String> roles; // role names only
}
