package fr.rawz06.starter.common.service;

import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean authenticate(String login, String password) {
        return userRepository.findByLogin(login)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public User createUser(String login, String plainPassword) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(plainPassword));
        return userRepository.save(user);
    }
}
