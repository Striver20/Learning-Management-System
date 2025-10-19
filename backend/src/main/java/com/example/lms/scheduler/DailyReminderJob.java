package com.example.lms.scheduler;

import com.example.lms.entity.Enrollment;
import com.example.lms.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Quartz Job that runs daily to send reminders to students about their enrolled courses
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReminderJob implements Job {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("🔔 Daily Reminder Job started at: {}", LocalDateTime.now());

        try {
            // Find all active enrollments
            List<Enrollment> activeEnrollments = enrollmentRepository.findAll();

            log.info("📧 Processing reminders for {} enrollments", activeEnrollments.size());

            for (Enrollment enrollment : activeEnrollments) {
                sendReminder(enrollment);
            }

            log.info("✅ Daily Reminder Job completed successfully");

        } catch (Exception e) {
            log.error("❌ Error in Daily Reminder Job: {}", e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }

    /**
     * Sends a reminder to the student about their course
     * In production, this would integrate with email service (AWS SES, SendGrid, etc.)
     */
    private void sendReminder(Enrollment enrollment) {
        String studentEmail = enrollment.getStudent().getEmail();
        String courseName = enrollment.getCourse().getTitle();

        // Simulate sending reminder (in production, integrate with email service)
        log.info("📬 Reminder sent to: {} for course: {}", studentEmail, courseName);

        // TODO: Integrate with actual email service
        // Example: emailService.send(studentEmail, "Course Reminder", buildEmailContent(enrollment));
    }
}

