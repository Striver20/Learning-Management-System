package com.example.lms.controller;

import com.example.lms.dto.CourseDTO;
import com.example.lms.dto.request.CourseRequest;
import com.example.lms.entity.Course;
import com.example.lms.entity.User;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.service.CourseService;
import com.example.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    // ✅ Create a new course (Teacher only)
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseRequest request,
                                                  @RequestParam String instructorEmail) {
        User instructor = userService.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        var course = courseService.createCourse(request, instructor);
        return ResponseEntity.ok(EntityMapper.toCourseDTO(course));
    }

    // ✅ List all courses
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(
                courseService.findAllCourses().stream()
                        .map(EntityMapper::toCourseDTO)
                        .toList()
        );
    }

    // ✅ Get courses by instructor
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

    // ✅ Get single course by ID
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        var course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return ResponseEntity.ok(EntityMapper.toCourseDTO(course));
    }

    // ✅ Delete a course (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course and related data deleted successfully");
    }

}
