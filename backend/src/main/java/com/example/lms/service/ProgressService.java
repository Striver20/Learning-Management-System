package com.example.lms.service;

import com.example.lms.entity.Content;
import com.example.lms.entity.Enrollment;
import com.example.lms.entity.Progress;
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

        return progressRepo.save(progress);

    }

    public List<Progress> getProgressByEnrollment(Enrollment enrollment){
        return progressRepo.findByEnrollment(enrollment);
    }
}
