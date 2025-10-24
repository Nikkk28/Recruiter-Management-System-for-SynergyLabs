package org.synlabs.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.synlabs.assignment.dto.ApiResponse;
import org.synlabs.assignment.model.Job;
import org.synlabs.assignment.model.User;
import org.synlabs.assignment.repository.JobRepository;
import org.synlabs.assignment.repository.UserRepository;
import org.synlabs.assignment.security.UserDetailsImpl;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllJobs() {
        try {
            List<Job> jobs = jobRepository.findAllByOrderByPostedOnDesc();
            return ResponseEntity.ok(new ApiResponse(true, "Jobs fetched successfully", jobs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching jobs: " + e.getMessage()));
        }
    }

    @GetMapping("/apply")
    public ResponseEntity<?> applyToJob(
            @RequestParam("job_id") Long jobId,
            Authentication authentication) {
        try {
            // Get current user
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User applicant = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get job
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            // Check if already applied
            if (job.getApplicants().contains(applicant)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "You have already applied to this job"));
            }

            // Add applicant to job
            job.getApplicants().add(applicant);
            job.setTotalApplications(job.getApplicants().size());
            jobRepository.save(job);

            return ResponseEntity.ok(new ApiResponse(true,
                    "Successfully applied to job: " + job.getTitle()));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error applying to job: " + e.getMessage()));
        }
    }
}