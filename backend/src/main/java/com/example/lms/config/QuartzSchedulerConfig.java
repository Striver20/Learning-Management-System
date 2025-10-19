package com.example.lms.config;

import com.example.lms.scheduler.DailyReminderJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz Scheduler Configuration
 * Configures daily reminder job to run every day at 9:00 AM
 */
@Configuration
public class QuartzSchedulerConfig {

    /**
     * JobDetail defines what job to run
     */
    @Bean
    public JobDetail dailyReminderJobDetail() {
        return JobBuilder.newJob(DailyReminderJob.class)
                .withIdentity("dailyReminderJob")
                .withDescription("Send daily reminders to students about their courses")
                .storeDurably()
                .build();
    }

    /**
     * Trigger defines when to run the job
     * Cron expression: "0 0 9 * * ?" means every day at 9:00 AM
     * For testing, you can use: "0 * * * * ?" (every minute)
     */
    @Bean
    public Trigger dailyReminderTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0 0 9 * * ?") // Every day at 9:00 AM
                .withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
                .forJob(dailyReminderJobDetail())
                .withIdentity("dailyReminderTrigger")
                .withDescription("Trigger for daily reminder job")
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * Alternative trigger for testing (runs every 5 minutes)
     * Uncomment this and comment the above trigger to test
     */
    /*
    @Bean
    public Trigger dailyReminderTestTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(dailyReminderJobDetail())
                .withIdentity("dailyReminderTestTrigger")
                .withDescription("Test trigger - runs every 5 minutes")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(5)
                        .repeatForever())
                .build();
    }
    */
}

