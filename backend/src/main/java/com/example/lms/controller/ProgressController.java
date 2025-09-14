package com.example.lms.controller;

import com.example.lms.dto.ProgressDTO;
import com.example.lms.dto.request.UpdateProgressRequest;
import com.example.lms.entity.Content;
import com.example.lms.entity.Course;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final EnrollmentService enrollmentService;
    private final ContentService contentService;
    private final UserService userService;
    private final CourseService courseService;

    // ✅ Update progress
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

    // ✅ Get all progress for a student in a course
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
