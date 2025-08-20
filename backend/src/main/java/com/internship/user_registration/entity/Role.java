package com.internship.user_registration.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore  // This prevents circular reference in JSON serialization
    @ToString.Exclude // Prevents circular reference in toString
    @EqualsAndHashCode.Exclude // Prevents circular reference in equals/hashCode
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // Predefined role names
    public static final String GENERAL_USER = "General User";
    public static final String PROFESSIONAL = "Professional";
    public static final String BUSINESS_OWNER = "Business Owner";
    public static final String ADMIN = "Admin";

    //Predefined role descriptions
    public static final String GENERAL_USER_DESCRIPTION = "Standard user with basic access permissions";
    public static final String PROFESSIONAL_DESCRIPTION = "Professional user with advanced features access";
    public static final String BUSINESS_OWNER_DESCRIPTION = "Business owner with full business management capabilities";
    public static final String ADMIN_DESCRIPTION = "System administrator with full access";

    public Role(String name) {
        this.name = name;
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}