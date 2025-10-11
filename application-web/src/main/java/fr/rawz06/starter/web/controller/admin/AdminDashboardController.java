package fr.rawz06.starter.web.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "anonymous";
        return Map.of(
                "message", "Welcome to admin dashboard",
                "user", user,
                "stats", Map.of(
                        "totalUsers", 42,
                        "activeUsers", 12,
                        "totalPosts", 156
                )
        );
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
