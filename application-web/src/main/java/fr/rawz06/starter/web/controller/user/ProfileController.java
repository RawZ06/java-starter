package fr.rawz06.starter.web.controller.user;

import fr.rawz06.starter.api.controller.UserApi;
import fr.rawz06.starter.api.dto.ErrorDto;
import fr.rawz06.starter.api.dto.UpdateProfileRequestDto;
import fr.rawz06.starter.api.dto.UserProfileDto;
import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.service.UserService;
import fr.rawz06.starter.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class ProfileController implements UserApi {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<UserProfileDto> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        User user = userService.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(userMapper.toUserProfileDto(user));
    }

    @Override
    public ResponseEntity<UserProfileDto> updateUserProfile(UpdateProfileRequestDto updateProfileRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        User user = userService.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Use MapStruct to update allowed fields (email, firstName, lastName)
        userMapper.updateUserFromProfileDto(updateProfileRequestDto, user);

        // Handle password change manually (requires validation and encoding)
        if (updateProfileRequestDto.getCurrentPassword() != null && updateProfileRequestDto.getNewPassword() != null) {
            if (!passwordEncoder.matches(updateProfileRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(updateProfileRequestDto.getNewPassword()));
        }

        User updatedUser = userService.update(user.getId(), user);

        return ResponseEntity.ok(userMapper.toUserProfileDto(updatedUser));
    }
}
