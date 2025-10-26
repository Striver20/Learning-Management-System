package com.example.lms.controller;

import com.example.lms.dto.ContentDTO;
import com.example.lms.dto.S3FileResponse;
import com.example.lms.entity.Content;
import com.example.lms.entity.Course;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.ContentService;
import com.example.lms.service.CourseService;
import com.example.lms.service.LocalFileStorageService;
import com.example.lms.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Course content management with AWS S3 file upload integration")
public class ContentController {

    private final ContentService contentService;
    private final CourseService courseService;
    private final S3Service s3Service;
    private final LocalFileStorageService localFileStorageService;

    @Operation(summary = "Upload content with file to S3", 
            description = "Upload course content file to AWS S3 and store metadata (Teacher only)",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content uploaded successfully to AWS S3"),
            @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "500", description = "S3 upload failed")
    })
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/upload")
    public ResponseEntity<Content> uploadContent(
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String contentType,
            @RequestParam Integer orderIndex,
            @RequestParam("file") MultipartFile file) {

        S3FileResponse fileResponse;
        
        try {
            // Try S3 upload first
            fileResponse = s3Service.uploadFile(file);
            System.out.println("✅ File uploaded to S3: " + fileResponse.getFileUrl());
        } catch (Exception e) {
            // Fallback to local storage if S3 fails
            System.err.println("⚠️ S3 upload failed, using local storage: " + e.getMessage());
            fileResponse = localFileStorageService.uploadFile(file);
            System.out.println("✅ File saved locally: " + fileResponse.getFileUrl());
        }

        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Content content = Content.builder()
                .title(title)
                .description(description)
                .fileUrl(fileResponse.getFileUrl()) // ✅ use getter
                .s3Key(fileResponse.getKey())       // ✅ use getter
                .contentType(contentType)
                .orderIndex(orderIndex)
                .createdAt(LocalDateTime.now())
                .course(course)
                .build();

        return ResponseEntity.ok(contentService.save(content));
    }


    @Operation(summary = "Add content to course", description = "Add content metadata to a course",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content added successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PostMapping
    public ResponseEntity<ContentDTO> addContent(@RequestParam Long courseId,
                                                 @RequestBody ContentDTO contentDTO) {
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var savedContent = contentService.addContentToCourse(course, EntityMapper.toContent(contentDTO));
        return ResponseEntity.ok(EntityMapper.toContentDTO(savedContent));
    }

    @Operation(summary = "Get course contents", description = "Retrieve all content items for a specific course",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contents retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping
    public ResponseEntity<List<ContentDTO>> getContentsByCourse(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var contents = contentService.getContentsByCourse(course)
                .stream()
                .map(EntityMapper::toContentDTO)
                .toList();

        return ResponseEntity.ok(contents);
    }
    @Operation(summary = "Delete content", 
            description = "Delete content from database and AWS S3 (Teacher only)",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required"),
            @ApiResponse(responseCode = "404", description = "Content not found")
    })
    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContent(@PathVariable Long id) {
        Content content = contentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        try {
            // Try deleting from S3 first
            if (content.getS3Key() != null) {
                s3Service.deleteFile(content.getS3Key());
                System.out.println("✅ Deleted from S3: " + content.getS3Key());
            }
        } catch (Exception e) {
            // If S3 fails, try local storage
            System.err.println("⚠️ S3 delete failed, trying local storage: " + e.getMessage());
            try {
                if (content.getS3Key() != null) {
                    localFileStorageService.deleteFile(content.getS3Key());
                    System.out.println("✅ Deleted from local storage: " + content.getS3Key());
                }
            } catch (Exception localException) {
                System.err.println("⚠️ Local file delete also failed: " + localException.getMessage());
            }
        }

        // Always delete from DB
        contentService.deleteContent(id);

        return ResponseEntity.ok("Content deleted successfully");
    }


}
