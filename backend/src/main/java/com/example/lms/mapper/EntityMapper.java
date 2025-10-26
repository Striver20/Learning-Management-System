package com.example.lms.mapper;

import com.example.lms.dto.*;
import com.example.lms.entity.*;

import java.util.Collections;
import java.util.stream.Collectors;


public class EntityMapper {

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    public static CourseDTO toCourseDTO(Course course) {
        String instructorName = null;
        String instructorEmail = null;

        if (course.getInstructor() != null) {
            // lazy-safe: only grab scalars
            instructorName = course.getInstructor().getFullName();
            instructorEmail = course.getInstructor().getEmail();
        }

        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                instructorName,
                instructorEmail,
                course.getContents() != null
                        ? course.getContents().stream()
                        .map(EntityMapper::toContentDTO)
                        .collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }

    public static ContentDTO toContentDTO(Content content) {
        return new ContentDTO(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getFileUrl(),
                content.getContentType(),
                content.getOrderIndex() != null ? content.getOrderIndex() : 0
        );
    }

    public static Content toContent(ContentDTO dto) {
        return Content.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .fileUrl(dto.getFileUrl())
                .contentType(dto.getContentType())
                .orderIndex(dto.getOrderIndex())
                .build();
    }

    public static EnrollmentDTO toEnrollmentDTO(Enrollment enrollment) {
        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId()) // <-- fix
                .courseTitle(enrollment.getCourse().getTitle())
                .studentEmail(enrollment.getStudent().getEmail()) // <-- fix
                .studentName(enrollment.getStudent().getFullName())
                .status(enrollment.getStatus().name())
                .progressPercentage(enrollment.getProgressPercentage())
                .build();
    }


    public static ProgressDTO toProgressDTO(Progress progress) {
        return ProgressDTO.builder()
                .id(progress.getId())
                .contentId(progress.getContent().getId())  // ✅ Added to identify which content
                .courseTitle(progress.getEnrollment().getCourse().getTitle())
                .contentTitle(progress.getContent().getTitle())
                .percentComplete(progress.getPercentComplete())
                .completed(progress.getCompleted())
                .build();
    }

}
