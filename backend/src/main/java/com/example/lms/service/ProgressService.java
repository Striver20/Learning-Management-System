package com.example.lms.service;

import com.example.lms.entity.Content;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.Progress;
import com.example.lms.repository.EnrollmentRepository;
import com.example.lms.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final ContentService contentService;

    public Progress updateProgress(Enrollment enrollment, Content content, Integer percentComplete){
        Optional<Progress> existing = progressRepo.findByEnrollmentAndContent(enrollment,content);

        Progress progress = existing.orElse(
                Progress.builder()
                        .enrollment(enrollment)
                        .content(content)
                        .percentComplete(0)
                        .completed(false)
                        .build()
        );

        progress.setPercentComplete(percentComplete);
        progress.setCompleted(percentComplete>=100);
        progress.setLastAccessedAt(LocalDateTime.now());

        Progress savedProgress = progressRepo.save(progress);

        // ✅ Recalculate and update enrollment's overall progress percentage
        updateEnrollmentProgress(enrollment);

        return savedProgress;
    }

    /**
     * Recalculates the overall course progress for an enrollment and updates it
     */
    private void updateEnrollmentProgress(Enrollment enrollment) {
        // Get all content for this course
        List<Content> allContent = contentService.getContentsByCourse(enrollment.getCourse());
        int totalLessons = allContent.size();

        if (totalLessons == 0) {
            enrollment.setProgressPercentage(0.0);
            enrollmentRepo.save(enrollment);
            return;
        }

        // Get all progress records for this enrollment
        List<Progress> progressList = progressRepo.findByEnrollment(enrollment);

        // Count completed lessons (percentComplete == 100)
        long completedLessons = progressList.stream()
                .filter(p -> p.getPercentComplete() >= 100)
                .count();

        // Calculate percentage
        double progressPercentage = ((double) completedLessons / totalLessons) * 100.0;
        
        // Round to 2 decimal places
        progressPercentage = Math.round(progressPercentage * 100.0) / 100.0;

        enrollment.setProgressPercentage(progressPercentage);
        enrollmentRepo.save(enrollment);

        System.out.println("✅ Updated enrollment progress: " + completedLessons + "/" + totalLessons + " = " + progressPercentage + "%");
    }

    public List<Progress> getProgressByEnrollment(Enrollment enrollment){
        return progressRepo.findByEnrollment(enrollment);
    }
}
