package com.example.lms.service;

import com.example.lms.dto.request.CourseRequest;
import com.example.lms.entity.Course;
import com.example.lms.entity.User;
import com.example.lms.exception.ConflictException;
import com.example.lms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final ContentService contentService;
    private final EnrollmentService enrollmentService;
    private final S3Service s3Service;


    public Course createCourse(CourseRequest request, User instructor) {
        if (courseRepo.findByTitleAndInstructor(request.getTitle(), instructor).isPresent()) {
            throw new ConflictException("Course with this title already exists for this instructor");
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .instructor(instructor)
                .build();

        return courseRepo.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));


        var contents = contentService.getContentsByCourse(course);
        for (var c : contents) {
            try {
                if (c.getS3Key() != null) {
                    s3Service.deleteFile(c.getS3Key());
                }
            } catch (Exception e) {
                System.err.println("⚠️ Failed to delete S3 file: " + e.getMessage());
            }
            contentService.deleteContent(c.getId());
        }

        // Delete course (DB will cascade delete enrollments automatically)
        courseRepo.delete(course);
    }


    public List<Course> findByInstructor(User instructor) {
        return courseRepo.findByInstructor(instructor);
    }

    public Optional<Course> findById(Long id) {
        return courseRepo.findByIdWithInstructorAndContents(id);
    }


    public List<Course> findAllCourses() {
        return courseRepo.findAll();
    }
    public void deleteById(Long id) { courseRepo.deleteById(id); }

}
