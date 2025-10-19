package com.example.lms.controller;

import com.example.lms.dto.ProgressDTO;
import com.example.lms.dto.request.UpdateProgressRequest;
import com.example.lms.entity.Content;
import com.example.lms.entity.Course;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Student progress tracking for course content")
public class ProgressController {

    private final ProgressService progressService;
    private final EnrollmentService enrollmentService;
    private final ContentService contentService;
    private final UserService userService;
    private final CourseService courseService;

    @Operation(summary = "Update student progress", 
            description = "Update student's progress for a specific content item",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
            @ApiResponse(responseCode = "404", description = "Student, course, enrollment, or content not found")
    })
    @PostMapping("/update")
    public ResponseEntity<ProgressDTO> updateProgress(@RequestBody UpdateProgressRequest request) {
        User student = userService.findByEmail(request.getStudentEmail())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseService.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Enrollment enrollment = enrollmentService.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        Content content = contentService.getContentsByCourse(course).stream()
                .filter(c -> c.getId().equals(request.getContentId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Content not found"));

        return ResponseEntity.ok(
                EntityMapper.toProgressDTO(progressService.updateProgress(enrollment, content, request.getPercentComplete()))
        );
    }

    @Operation(summary = "Get student progress", 
            description = "Retrieve all progress records for a student in a specific course",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student, course, or enrollment not found")
    })
    @GetMapping
    public ResponseEntity<List<ProgressDTO>> getProgress(@RequestParam String studentEmail,
                                                         @RequestParam Long courseId) {
        User student = userService.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Enrollment enrollment = enrollmentService.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        return ResponseEntity.ok(
                progressService.getProgressByEnrollment(enrollment).stream()
                        .map(EntityMapper::toProgressDTO)
                        .toList()
        );
    }
}
