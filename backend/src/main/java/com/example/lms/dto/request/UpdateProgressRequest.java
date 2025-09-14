package com.example.lms.dto.request;

import lombok.Data;

@Data
public class UpdateProgressRequest {
    private String studentEmail;
    private Long courseId;
    private Long contentId;
    private Integer percentComplete;
}
