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
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final RoleRepository roleRepository;

    // ✅ Get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(
                userService.findAllUsers()
                        .stream()
                        .map(EntityMapper::toUserDTO)
                        .toList()
        );
    }

    // ✅ Assign role to user
    @PostMapping("/users/{userId}/role")
    public ResponseEntity<UserDTO> assignRole(@PathVariable Long userId, @RequestParam String roleName) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = roleRepository.findByRoleName(RoleName.valueOf(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.getRoles().add(role);
        return ResponseEntity.ok(EntityMapper.toUserDTO(userService.save(user)));
    }

    // ✅ Delete user
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    // ✅ Get all courses
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(
                courseService.findAllCourses()
                        .stream()
                        .map(EntityMapper::toCourseDTO)
                        .toList()
        );
    }

    // ✅ Delete course
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteById(courseId);
        return ResponseEntity.ok("Course deleted successfully");
    }

    // ✅ Get all enrollments
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.findAll()
                        .stream()
                        .map(EntityMapper::toEnrollmentDTO)
                        .toList()
        );
    }

    // ✅ Update enrollment status
    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<EnrollmentDTO> updateEnrollmentStatus(@PathVariable Long enrollmentId,
                                                                @RequestParam String status) {
        Enrollment enrollment = enrollmentService.updateStatus(enrollmentId, status);
        return ResponseEntity.ok(EntityMapper.toEnrollmentDTO(enrollment));
    }
}
