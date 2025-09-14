package com.example.lms.controller;

import com.example.lms.dto.ContentDTO;
import com.example.lms.dto.S3FileResponse;
import com.example.lms.entity.Content;
import com.example.lms.entity.Course;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.ContentService;
import com.example.lms.service.CourseService;
import com.example.lms.service.S3Service;
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
public class ContentController {

    private final ContentService contentService;
    private final CourseService courseService;
    private final S3Service s3Service;

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/upload")
    public ResponseEntity<Content> uploadContent(
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String contentType,
            @RequestParam Integer orderIndex,
            @RequestParam("file") MultipartFile file) {

        // Upload file to S3 → returns object with url + key
        S3FileResponse s3Response = s3Service.uploadFile(file);

        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Content content = Content.builder()
                .title(title)
                .description(description)
                .fileUrl(s3Response.getFileUrl()) // ✅ use getter
                .s3Key(s3Response.getKey())       // ✅ use getter
                .contentType(contentType)
                .orderIndex(orderIndex)
                .createdAt(LocalDateTime.now())
                .course(course)
                .build();

        return ResponseEntity.ok(contentService.save(content));
    }


    // Add content to a course
    @PostMapping
    public ResponseEntity<ContentDTO> addContent(@RequestParam Long courseId,
                                                 @RequestBody ContentDTO contentDTO) {
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var savedContent = contentService.addContentToCourse(course, EntityMapper.toContent(contentDTO));
        return ResponseEntity.ok(EntityMapper.toContentDTO(savedContent));
    }

    // Get contents of a course
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
    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContent(@PathVariable Long id) {
        Content content = contentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        try {
            // Try deleting from S3
            s3Service.deleteFile(content.getS3Key());
        } catch (Exception e) {
            // Log a warning but don't block DB deletion
            System.err.println("⚠️ Could not delete from S3 (maybe already deleted): " + e.getMessage());
        }

        // Always delete from DB
        contentService.deleteContent(id);

        return ResponseEntity.ok("Content deleted successfully (S3 file may have already been removed)");
    }


}
