package com.example.lms.dto.request;

import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private String category;
}
