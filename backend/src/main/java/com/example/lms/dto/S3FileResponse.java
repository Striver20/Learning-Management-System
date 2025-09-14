package com.example.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3FileResponse {
    private String fileUrl;
    private String key;
}

