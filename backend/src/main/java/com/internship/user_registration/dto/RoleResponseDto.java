package com.internship.user_registration.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {

    private Long roleId;
    private String name;
    private String description;
    private Long userCount; // Optional: number of users with this role
}