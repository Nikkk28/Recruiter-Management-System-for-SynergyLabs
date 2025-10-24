package org.synlabs.assignment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.synlabs.assignment.dto.ApiResponse;
import org.synlabs.assignment.dto.JobRequest;
import org.synlabs.assignment.model.Job;
import org.synlabs.assignment.model.Profile;
import org.synlabs.assignment.model.User;
import org.synlabs.assignment.model.enums.UserType;
import org.synlabs.assignment.repository.JobRepository;
import org.synlabs.assignment.repository.ProfileRepository;
import org.synlabs.assignment.repository.UserRepository;
import org.synlabs.assignment.security.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @PostMapping("/job")
    public ResponseEntity<?> createJob(
            @Valid @RequestBody JobRequest jobRequest,
            Authentication authentication) {
        try {
            // Get current admin user
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User admin = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create new job
            Job job = new Job();
            job.setTitle(jobRequest.getTitle());
            job.setDescription(jobRequest.getDescription());
            job.setCompanyName(jobRequest.getCompanyName());
            job.setPostedOn(LocalDateTime.now());
            job.setPostedBy(admin);
            job.setTotalApplications(0);

            jobRepository.save(job);

            return ResponseEntity.ok(new ApiResponse(true,
                    "Job created successfully", job));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating job: " + e.getMessage()));
        }
    }

    @GetMapping("/job/{job_id}")
    public ResponseEntity<?> getJobDetails(@PathVariable("job_id") Long jobId) {
        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            // Create response with job details and applicants
            Map<String, Object> response = new HashMap<>();
            response.put("id", job.getId());
            response.put("title", job.getTitle());
            response.put("description", job.getDescription());
            response.put("companyName", job.getCompanyName());
            response.put("postedOn", job.getPostedOn());
            response.put("totalApplications", job.getTotalApplications());
            response.put("postedBy", Map.of(
                    "id", job.getPostedBy().getId(),
                    "name", job.getPostedBy().getName(),
                    "email", job.getPostedBy().getEmail()
            ));

            // Get applicant details
            List<Map<String, Object>> applicants = job.getApplicants().stream()
                    .map(applicant -> {
                        Map<String, Object> applicantData = new HashMap<>();
                        applicantData.put("id", applicant.getId());
                        applicantData.put("name", applicant.getName());
                        applicantData.put("email", applicant.getEmail());
                        applicantData.put("profileHeadline", applicant.getProfileHeadline());
                        return applicantData;
                    })
                    .collect(Collectors.toList());

            response.put("applicants", applicants);

            return ResponseEntity.ok(new ApiResponse(true,
                    "Job details fetched successfully", response));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching job details: " + e.getMessage()));
        }
    }

    @GetMapping("/applicants")
    public ResponseEntity<?> getAllApplicants() {
        try {
            List<User> applicants = userRepository.findByUserType(UserType.APPLICANT);

            List<Map<String, Object>> applicantList = applicants.stream()
                    .map(applicant -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", applicant.getId());
                        data.put("name", applicant.getName());
                        data.put("email", applicant.getEmail());
                        data.put("address", applicant.getAddress());
                        data.put("profileHeadline", applicant.getProfileHeadline());
                        return data;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true,
                    "Applicants fetched successfully", applicantList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching applicants: " + e.getMessage()));
        }
    }

    @GetMapping("/applicant/{applicant_id}")
    public ResponseEntity<?> getApplicantDetails(@PathVariable("applicant_id") Long applicantId) {
        try {
            User applicant = userRepository.findById(applicantId)
                    .orElseThrow(() -> new RuntimeException("Applicant not found"));

            if (applicant.getUserType() != UserType.APPLICANT) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User is not an applicant"));
            }

            Profile profile = profileRepository.findByApplicantId(applicantId)
                    .orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("id", applicant.getId());
            response.put("name", applicant.getName());
            response.put("email", applicant.getEmail());
            response.put("address", applicant.getAddress());
            response.put("profileHeadline", applicant.getProfileHeadline());

            if (profile != null) {
                Map<String, Object> profileData = new HashMap<>();
                profileData.put("resumeFileAddress", profile.getResumeFileAddress());
                profileData.put("skills", profile.getSkills());
                profileData.put("education", profile.getEducation());
                profileData.put("experience", profile.getExperience());
                profileData.put("name", profile.getName());
                profileData.put("email", profile.getEmail());
                profileData.put("phone", profile.getPhone());
                response.put("profile", profileData);
            } else {
                response.put("profile", null);
            }

            return ResponseEntity.ok(new ApiResponse(true,
                    "Applicant details fetched successfully", response));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching applicant details: " + e.getMessage()));
        }
    }
}