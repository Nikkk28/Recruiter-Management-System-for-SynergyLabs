package org.synlabs.assignment.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "profiles")
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne @JoinColumn(name = "applicant_id", nullable = false, unique = true)
    private User applicant;
    private String resumeFileAddress;
    @Column(columnDefinition = "TEXT")
    private String skills;
    @Column(columnDefinition = "TEXT")
    private String education;
    @Column(columnDefinition = "TEXT")
    private String experience;
    private String name;
    private String email;
    private String phone;
}
