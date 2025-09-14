package com.example.lms.repository;

import com.example.lms.entity.Content;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress,Long> {
    List<Progress> findByEnrollment(Enrollment enrollment);
    Optional<Progress> findByEnrollmentAndContent(Enrollment enrollment, Content content);
}
