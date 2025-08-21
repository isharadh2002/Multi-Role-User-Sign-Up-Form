package com.internship.user_registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}