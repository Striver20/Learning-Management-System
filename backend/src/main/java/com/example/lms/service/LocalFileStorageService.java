package com.example.lms.service;

import com.example.lms.dto.S3FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local file storage service for development/testing when S3 is not available
 */
@Service
@Slf4j
public class LocalFileStorageService {

    private static final String UPLOAD_DIR = "uploads";

    public LocalFileStorageService() {
        // Create uploads directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("✅ Created uploads directory: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("❌ Failed to create uploads directory", e);
        }
    }

    public S3FileResponse uploadFile(MultipartFile file) {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "-" + originalFilename;

            // Save file locally
            Path targetPath = Paths.get(UPLOAD_DIR, uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Create a local URL (for development)
            String fileUrl = "http://localhost:8080/uploads/" + uniqueFilename;

            log.info("✅ File saved locally: {}", targetPath.toAbsolutePath());
            log.info("📁 Access URL: {}", fileUrl);

            return new S3FileResponse(fileUrl, uniqueFilename);

        } catch (IOException e) {
            log.error("❌ Failed to save file locally", e);
            throw new RuntimeException("Failed to store file locally", e);
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.deleteIfExists(filePath);
            log.info("✅ File deleted: {}", filename);
        } catch (IOException e) {
            log.error("❌ Failed to delete file: {}", filename, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public byte[] getFile(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("❌ Failed to read file: {}", filename, e);
            throw new RuntimeException("File not found", e);
        }
    }
}

