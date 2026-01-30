package fr.rawz06.starter.web.mapper;

import fr.rawz06.starter.api.dto.*;
import fr.rawz06.starter.common.entity.User;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity → DTO mappings
    @Mapping(target = "role", source = "role", qualifiedByName = "stringToRoleEnum")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    UserDto toUserDto(User user);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    UserProfileDto toUserProfileDto(User user);

    // DTO → Entity mappings (for creation)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "createRoleEnumToString")
    User toUser(CreateUserRequestDto dto);

    // DTO → Entity mappings (for update with @MappingTarget)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "updateRoleEnumToString")
    void updateUserFromDto(UpdateUserRequestDto dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromProfileDto(UpdateProfileRequestDto dto, @MappingTarget User user);

    // Helper methods for enum/string conversions
    @Named("stringToRoleEnum")
    default UserDto.RoleEnum stringToRoleEnum(String role) {
        if (role == null) {
            return null;
        }
        return UserDto.RoleEnum.fromValue(role);
    }

    @Named("createRoleEnumToString")
    default String createRoleEnumToString(CreateUserRequestDto.RoleEnum roleEnum) {
        if (roleEnum == null) {
            return "USER";
        }
        return roleEnum.getValue();
    }

    @Named("updateRoleEnumToString")
    default String updateRoleEnumToString(UpdateUserRequestDto.RoleEnum roleEnum) {
        if (roleEnum == null) {
            return null;
        }
        return roleEnum.getValue();
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
