package com.example.lms.controller;

import com.example.lms.entity.Course;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.CourseService;
import com.example.lms.service.EnrollmentService;
import com.example.lms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.lms.dto.EnrollmentDTO;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Student enrollment management")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final CourseService courseService;

    @Operation(summary = "Enroll student in course", 
            description = "Enroll a student in a specific course (Student role required)",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student enrolled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Student role required"),
            @ApiResponse(responseCode = "404", description = "Student or course not found"),
            @ApiResponse(responseCode = "409", description = "Student already enrolled")
    })
    @PostMapping
    public ResponseEntity<Enrollment> enrollStudent(@RequestParam String studentEmail,
                                                    @RequestParam Long courseId) {
        User student = userService.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return ResponseEntity.ok(enrollmentService.enrollStudent(student, course));
    }

    @Operation(summary = "Get student enrollments", 
            description = "Retrieve all course enrollments for a specific student",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
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
