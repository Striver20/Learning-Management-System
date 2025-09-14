package com.example.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String contentType;
    private int orderIndex;
}
