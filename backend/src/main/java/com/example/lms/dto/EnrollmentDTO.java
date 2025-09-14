package com.example.lms.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class EnrollmentDTO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String studentEmail;
    private String studentName;
    private String status;
    private Double progressPercentage;
}
