package com.example.lms.controller;

import com.example.lms.entity.Course;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.CourseService;
import com.example.lms.service.EnrollmentService;
import com.example.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.lms.dto.EnrollmentDTO;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final CourseService courseService;

    // Enroll student by email & courseId
    @PostMapping
    public ResponseEntity<Enrollment> enrollStudent(@RequestParam String studentEmail,
                                                    @RequestParam Long courseId) {
        User student = userService.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return ResponseEntity.ok(enrollmentService.enrollStudent(student, course));
    }

    // Get all enrollments for a student
    @GetMapping("/student")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@RequestParam String email) {
        email = email.trim(); // 🧹 handle accidental newline

        User student = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<EnrollmentDTO> response = enrollmentService.findByStudent(student)
                .stream()
                .map(EntityMapper::toEnrollmentDTO)
                .toList();


        return ResponseEntity.ok(response);
    }


}
