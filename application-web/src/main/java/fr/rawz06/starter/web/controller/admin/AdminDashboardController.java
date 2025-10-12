package fr.rawz06.starter.web.controller.admin;

import fr.rawz06.starter.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";

        // Calculate real statistics
        long totalUsers = userService.findAll().size();
        long activeUsers = userService.findAll().stream()
                .filter(user -> user.getActive() != null && user.getActive())
                .count();

        long adminUsers = userService.findAll().stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .count();

        // Users created in the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentUsers = userService.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("recentUsers", recentUsers);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to admin dashboard");
        response.put("user", username);
        response.put("stats", stats);

        return response;
    }

    @GetMapping("/profile")
    public Map<String, String> getProfile(Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "anonymous";
        return Map.of(
                "username", user,
                "email", user + "@example.com",
                "role", "ADMIN"
        );
    }
}
