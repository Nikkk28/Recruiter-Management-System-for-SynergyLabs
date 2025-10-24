package org.synlabs.assignment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.synlabs.assignment.model.enums.UserType;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false)
    private String name;
    @Email @NotBlank @Column(unique = true, nullable = false)
    private String email;
    @NotBlank @Column(nullable = false)
    private String address;
    @Enumerated(EnumType.STRING) @NotBlank @Column(nullable = false)
    private UserType userType;
    @NotBlank @Column(nullable = false)
    private String passwordHash;
    @NotBlank @Column(nullable = false)
    private String profileHeadline;
    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile;
}
