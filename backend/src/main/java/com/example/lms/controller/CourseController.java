package com.example.lms.controller;

import com.example.lms.dto.CourseDTO;
import com.example.lms.dto.request.CourseRequest;
import com.example.lms.entity.Course;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.CourseService;
import com.example.lms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints - Create, retrieve, and delete courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @Operation(summary = "Create a new course", description = "Create a course (Teacher only)",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required"),
            @ApiResponse(responseCode = "404", description = "Instructor not found")
    })
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseRequest request,
                                                  @RequestParam String instructorEmail) {
        User instructor = userService.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        var course = courseService.createCourse(request, instructor);
        return ResponseEntity.ok(EntityMapper.toCourseDTO(course));
    }

    @Operation(summary = "Get all courses", description = "Retrieve list of all available courses")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(
                courseService.findAllCourses().stream()
                        .map(EntityMapper::toCourseDTO)
                        .toList()
        );
    }

    @Operation(summary = "Get courses by instructor", description = "Retrieve all courses taught by a specific instructor",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Instructor not found")
    })
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/instructor")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(@RequestParam String email) {
        User instructor = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        return ResponseEntity.ok(
                courseService.findByInstructor(instructor).stream()
                        .map(EntityMapper::toCourseDTO)
                        .toList()
        );
    }

    @Operation(summary = "Get course by ID", description = "Retrieve detailed information about a specific course",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        var course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return ResponseEntity.ok(EntityMapper.toCourseDTO(course));
    }

    @Operation(summary = "Delete a course", description = "Delete a course and all related data (Admin only)",
            security = @SecurityRequirement(name = "Bearer JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course and related data deleted successfully");
    }

}
