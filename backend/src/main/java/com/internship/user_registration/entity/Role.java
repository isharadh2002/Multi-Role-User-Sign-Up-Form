package com.internship.user_registration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // Predefined role names (using proper case as requested)
    public static final String GENERAL_USER = "General User";
    public static final String PROFESSIONAL = "Professional";
    public static final String BUSINESS_OWNER = "Business Owner";

    // Constructor for creating roles with name only
    public Role(String name) {
        this.name = name;
    }

    // Constructor for creating roles with name and description
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}