package fr.rawz06.starter.web.controller.user;

import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String login = authentication.getName();
        User user = userService.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("login", user.getLogin());
        profile.put("email", user.getEmail());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("role", user.getRole());
        profile.put("active", user.getActive());
        profile.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        String login = authentication.getName();
        User user = userService.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only allowed fields (not role, not login)
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        // Handle password change
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Current password is incorrect"));
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        User updatedUser = userService.update(user.getId(), user);

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", updatedUser.getId());
        profile.put("login", updatedUser.getLogin());
        profile.put("email", updatedUser.getEmail());
        profile.put("firstName", updatedUser.getFirstName());
        profile.put("lastName", updatedUser.getLastName());
        profile.put("role", updatedUser.getRole());
        profile.put("active", updatedUser.getActive());

        return ResponseEntity.ok(profile);
    }

    @Data
    public static class UpdateProfileRequest {
        private String email;
        private String firstName;
        private String lastName;
        private String currentPassword;
        private String newPassword;
    }
}
