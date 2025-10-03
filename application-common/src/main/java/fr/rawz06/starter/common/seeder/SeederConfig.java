package fr.rawz06.starter.common.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;

/**
 * Configuration class that orchestrates all seeders
 * Activated only when seeding.enabled=true in application.yml
 * Truncates tables and seeds data
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SeederConfig {

    @Bean
    @ConditionalOnProperty(name = "seeding.enabled", havingValue = "true")
    public CommandLineRunner runSeeders(List<Seeder<?>> seeders) {
        return args -> {
            log.info("🌱 Starting database seeding...");

            List<Seeder<?>> sortedSeeders = seeders.stream()
                    .sorted(Comparator.comparingInt(Seeder::getOrder))
                    .toList();

            log.info("⚠️  Truncating tables...");
            sortedSeeders.forEach(seeder -> {
                log.info("   ▶ Truncating: {}", seeder.getClass().getSimpleName());
                seeder.truncate();
            });

            sortedSeeders.forEach(seeder -> {
                log.info("   ▶ Running seeder: {}", seeder.getClass().getSimpleName());
                seeder.seed();
            });

            log.info("✅ Database seeding completed");
        };
    }
}
