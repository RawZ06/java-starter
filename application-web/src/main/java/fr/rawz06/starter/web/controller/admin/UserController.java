package fr.rawz06.starter.web.controller.admin;

import fr.rawz06.starter.api.controller.AdminUsersApi;
import fr.rawz06.starter.api.dto.*;
import fr.rawz06.starter.common.entity.User;
import fr.rawz06.starter.common.service.UserService;
import fr.rawz06.starter.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController implements AdminUsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        return userService.findById(id)
                .map(userMapper::toUserDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UserDto> createUser(CreateUserRequestDto createUserRequestDto) {
        try {
            User user = userMapper.toUser(createUserRequestDto);

            // Set default active if not provided
            if (user.getActive() == null) {
                user.setActive(true);
            }

            User created = userService.create(user);
            return ResponseEntity.ok(userMapper.toUserDto(created));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create user", e);
        }
    }

    @Override
    public ResponseEntity<UserDto> updateUser(Long id, UpdateUserRequestDto updateUserRequestDto) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Use MapStruct to update only non-null fields
            userMapper.updateUserFromDto(updateUserRequestDto, user);

            User updated = userService.update(id, user);
            return ResponseEntity.ok(userMapper.toUserDto(updated));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update user", e);
        }
    }

    @Override
    public ResponseEntity<MessageDto> deleteUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            // Prevent user from deleting themselves
            User userToDelete = userService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            if (authentication != null && authentication.getName().equals(userToDelete.getLogin())) {
                ErrorDto error = new ErrorDto();
                error.setError("You cannot delete your own account");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete own account");
            }

            userService.delete(id);

            MessageDto message = new MessageDto();
            message.setMessage("User deleted successfully");
            return ResponseEntity.ok(message);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to delete user", e);
        }
    }
}
