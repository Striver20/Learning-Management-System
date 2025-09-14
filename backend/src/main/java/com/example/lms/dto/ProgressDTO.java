package com.example.lms.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressDTO {
    private Long id;
    private String courseTitle;
    private String contentTitle;
    private Integer percentComplete;
    private Boolean completed;
}
