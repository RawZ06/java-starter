package fr.rawz06.starter.web.auth;

import fr.rawz06.starter.api.controller.AuthApi;
import fr.rawz06.starter.api.dto.LoginRequestDto;
import fr.rawz06.starter.api.dto.LoginResponseDto;
import fr.rawz06.starter.api.dto.UserDto;
import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.service.UserService;
import fr.rawz06.starter.web.mapper.UserMapper;
import fr.rawz06.starter.web.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        if (!userService.authenticate(username, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        // Get user role
        var user = userService.findByLogin(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(jwtService.generateToken(username, user.getRole()));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        User user = userService.findByLogin(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(userMapper.toUserDto(user));
    }
}
