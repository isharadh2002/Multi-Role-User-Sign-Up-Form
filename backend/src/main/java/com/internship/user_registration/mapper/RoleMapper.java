package com.internship.user_registration.mapper;

import com.internship.user_registration.dto.RoleResponseDto;
import com.internship.user_registration.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Role entities and DTOs
 * Follows the Mapper pattern for clean separation of concerns
 */
@Component
public class RoleMapper {

    /**
     * Convert Role entity to RoleResponseDto
     *
     * @param role the role entity
     * @return RoleResponseDto
     */
    public RoleResponseDto toResponseDto(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponseDto.builder()
                .roleId(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .userCount((long) role.getUsers().size()) // Safe to call since we avoid circular reference
                .build();
    }

    /**
     * Convert list of Role entities to list of RoleResponseDtos
     *
     * @param roles list of role entities
     * @return list of RoleResponseDtos
     */
    public List<RoleResponseDto> toResponseDtoList(List<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert set of Role entities to set of RoleResponseDtos
     *
     * @param roles set of role entities
     * @return set of RoleResponseDtos
     */
    public Set<RoleResponseDto> toResponseDtoSet(Set<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toSet());
    }

    /**
     * Convert Role entity to simple DTO without user count
     * Use this method when you want to avoid loading users collection
     *
     * @param role the role entity
     * @return RoleResponseDto without user count
     */
    public RoleResponseDto toResponseDtoWithoutUserCount(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponseDto.builder()
                .roleId(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .userCount(0L) // Set to 0 or null if not needed
                .build();
    }
}