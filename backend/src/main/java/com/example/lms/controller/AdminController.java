package com.example.lms.controller;

import com.example.lms.dto.UserDTO;
import com.example.lms.dto.CourseDTO;
import com.example.lms.dto.EnrollmentDTO;
import com.example.lms.entity.Course;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.Role;
import com.example.lms.entity.RoleName;
import com.example.lms.entity.User;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.mapper.EntityMapper;
import com.example.lms.repository.RoleRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative operations (Admin role required)")
@SecurityRequirement(name = "Bearer JWT")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final RoleRepository roleRepository;

    @Operation(summary = "Get all users", description = "Retrieve all registered users (Admin only)")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(
                userService.findAllUsers()
                        .stream()
                        .map(EntityMapper::toUserDTO)
                        .toList()
        );
    }

    @Operation(summary = "Assign role to user", description = "Assign a role (ADMIN, TEACHER, STUDENT) to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
            @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @PostMapping("/users/{userId}/role")
    public ResponseEntity<UserDTO> assignRole(@PathVariable Long userId, @RequestParam String roleName) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = roleRepository.findByRoleName(RoleName.valueOf(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.getRoles().add(role);
        return ResponseEntity.ok(EntityMapper.toUserDTO(userService.save(user)));
    }

    @Operation(summary = "Delete user", description = "Delete a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @Operation(summary = "Get all courses", description = "Retrieve all courses in the system")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(
                courseService.findAllCourses()
                        .stream()
                        .map(EntityMapper::toCourseDTO)
                        .toList()
        );
    }

    @Operation(summary = "Delete course", description = "Delete a course and all related data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteById(courseId);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @Operation(summary = "Get all enrollments", description = "Retrieve all student enrollments")
    @ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully")
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.findAll()
                        .stream()
                        .map(EntityMapper::toEnrollmentDTO)
                        .toList()
        );
    }

    @Operation(summary = "Update enrollment status", description = "Update the status of a student enrollment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<EnrollmentDTO> updateEnrollmentStatus(@PathVariable Long enrollmentId,
                                                                @RequestParam String status) {
        Enrollment enrollment = enrollmentService.updateStatus(enrollmentId, status);
        return ResponseEntity.ok(EntityMapper.toEnrollmentDTO(enrollment));
    }
}
