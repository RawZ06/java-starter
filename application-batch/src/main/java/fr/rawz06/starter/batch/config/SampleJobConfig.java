package fr.rawz06.starter.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SampleJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;

    @Bean
    public Job sampleJob() {
        return new JobBuilder("sampleJob", jobRepository)
                .start(sampleStep())
                .build();
    }

    @Bean
    public Step sampleStep() {
        return new StepBuilder("sampleStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Hello from Spring Batch! This is a sample job.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Scheduled(cron = "0 0/5 * * * *")// Every 5 minutes
    public void launchSampleJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(sampleJob(), params);
            log.info("Sample job launched successfully");
        } catch (Exception e) {
            log.error("Error launching sample job", e);
        }
    }
}
