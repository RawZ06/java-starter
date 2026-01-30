package fr.rawz06.starter.web.controller.admin;

import fr.rawz06.starter.api.controller.AdminDashboardApi;
import fr.rawz06.starter.api.dto.*;
import fr.rawz06.starter.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AdminDashboardController implements AdminDashboardApi {

    private final UserService userService;

    @Override
    public ResponseEntity<DashboardDto> getAdminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalUsers(totalUsers);
        stats.setActiveUsers(activeUsers);
        stats.setAdminUsers(adminUsers);
        stats.setRecentUsers(recentUsers);

        DashboardDto dashboard = new DashboardDto();
        dashboard.setMessage("Welcome to admin dashboard");
        dashboard.setUser(username);
        dashboard.setStats(stats);

        return ResponseEntity.ok(dashboard);
    }

    @Override
    public ResponseEntity<AdminProfileDto> getAdminProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication != null ? authentication.getName() : "anonymous";

        AdminProfileDto profile = new AdminProfileDto();
        profile.setUsername(user);
        profile.setEmail(user + "@example.com");
        profile.setRole("ADMIN");

        return ResponseEntity.ok(profile);
    }
}
