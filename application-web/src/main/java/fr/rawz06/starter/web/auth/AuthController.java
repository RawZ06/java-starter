package fr.rawz06.starter.web.auth;

import fr.rawz06.starter.web.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> payload) {
        // Demo: accepts any non-empty username/password
        String username = payload.getOrDefault("username", "");
        String password = payload.getOrDefault("password", "");

        if (username.isBlank() || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        return Map.of("token", jwtService.generateToken(username));
    }
}
