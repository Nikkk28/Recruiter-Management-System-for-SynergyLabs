package org.synlabs.assignment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "jobs")
public class Job {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false)
    private String title;
    @Lob @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(nullable = false)
    private Instant postedOn;
    private Integer totalApplicants=0;
    @Column(nullable = false)
    private String companyName;
    @ManyToOne @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;
    @ManyToMany
    @JoinTable(
            name = "job_applications",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "applicant_id")
    )
    private Set<User> applicants = new HashSet<>();
}
