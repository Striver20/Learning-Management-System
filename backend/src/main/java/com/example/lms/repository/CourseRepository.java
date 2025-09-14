package com.example.lms.repository;

import com.example.lms.entity.Course;
import com.example.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitleAndInstructor(String title, User instructor);

    List<Course> findByInstructor(User instructor);

    // ✅ Custom fetch to avoid LazyInitialization / ByteBuddy errors
    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.instructor " +
            "LEFT JOIN FETCH c.contents " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithInstructorAndContents(@Param("id") Long id);
}
