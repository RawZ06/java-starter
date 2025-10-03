package fr.rawz06.starter.common.seeder;

import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeder for User entities
 * Creates default admin user for development
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements Seeder<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void truncate() {
        userRepository.deleteAll();
        log.info("     🗑️  Users table truncated");
    }

    @Override
    public void seed() {
        User admin = new User();
        admin.setLogin("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);
        log.info("     ✓ Admin user created (login: admin, password: admin)");
    }

    @Override
    public int getOrder() {
        return 10; // Execute early
    }
}
