package com.example.lms.repository;

import com.example.lms.entity.Content;
import com.example.lms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content,Long> {
    List<Content> findByCourse(Course course);
    List<Content> findByCourseId(Long courseId);
}
