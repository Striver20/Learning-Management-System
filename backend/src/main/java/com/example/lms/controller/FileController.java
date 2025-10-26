package com.example.lms.controller;

import com.example.lms.service.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to serve locally stored files
 */
@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class FileController {

    private final LocalFileStorageService localFileStorageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            byte[] data = localFileStorageService.getFile(filename);
            ByteArrayResource resource = new ByteArrayResource(data);

            // Determine content type based on file extension
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String filename) {
        if (filename.endsWith(".pdf")) return "application/pdf";
        if (filename.endsWith(".mp4")) return "video/mp4";
        if (filename.endsWith(".mp3")) return "audio/mpeg";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".doc")) return "application/msword";
        if (filename.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (filename.endsWith(".txt")) return "text/plain";
        if (filename.endsWith(".zip")) return "application/zip";
        return "application/octet-stream";
    }
}

