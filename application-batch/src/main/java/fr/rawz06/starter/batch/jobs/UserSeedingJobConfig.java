package fr.rawz06.starter.batch.jobs;

import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserSeedingJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public Job userSeedingJob() {
        return new JobBuilder("userSeedingJob", jobRepository)
                .start(seedUsersStep())
                .build();
    }

    @Bean
    public Step seedUsersStep() {
        return new StepBuilder("seedUsersStep", jobRepository)
                .tasklet(seedUsersTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet seedUsersTasklet() {
        return (contribution, chunkContext) -> {
            log.info("🌱 Starting user seeding...");

            // Check if users already exist
            if (userRepository.count() > 0) {
                log.info("✅ Users already exist, skipping seeding");
                return RepeatStatus.FINISHED;
            }

            // Create admin user
            User admin = new User();
            admin.setLogin("admin");
            admin.setEmail("admin@starter.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole("ADMIN");
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            log.info("✅ Created admin user");

            // Create regular user
            User user = new User();
            user.setLogin("user");
            user.setEmail("user@starter.com");
            user.setPassword(passwordEncoder.encode("user"));
            user.setFirstName("Regular");
            user.setLastName("User");
            user.setRole("USER");
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("✅ Created regular user");

            log.info("🎉 User seeding completed successfully!");
            return RepeatStatus.FINISHED;
        };
    }
}
