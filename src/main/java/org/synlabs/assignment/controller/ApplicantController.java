package org.synlabs.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.synlabs.assignment.dto.ApiResponse;
import org.synlabs.assignment.dto.ResumeParserResponse;
import org.synlabs.assignment.model.Profile;
import org.synlabs.assignment.model.User;
import org.synlabs.assignment.repository.ProfileRepository;
import org.synlabs.assignment.repository.UserRepository;
import org.synlabs.assignment.security.UserDetailsImpl;
import org.synlabs.assignment.service.FileStorageService;
import org.synlabs.assignment.service.ResumeParserService;

@RestController
@RequestMapping
public class ApplicantController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ResumeParserService resumeParserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @PostMapping("/uploadResume")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            if (!fileStorageService.isValidFileType(file)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Only PDF and DOCX files are allowed"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String filename = fileStorageService.storeFile(file, user.getEmail());

            byte[] fileBytes = file.getBytes();
            ResumeParserResponse parserResponse = resumeParserService.parseResume(fileBytes);

            Profile profile = profileRepository.findByApplicant(user)
                    .orElse(new Profile());

            profile.setApplicant(user);
            profile.setResumeFileAddress(filename);

            if (parserResponse != null) {
                profile.setName(parserResponse.getName());
                profile.setEmail(parserResponse.getEmail());
                profile.setPhone(parserResponse.getPhone());
                profile.setSkills(resumeParserService.formatSkills(parserResponse));
                profile.setEducation(resumeParserService.formatEducation(parserResponse));
                profile.setExperience(resumeParserService.formatExperience(parserResponse));
            }

            profileRepository.save(profile);

            return ResponseEntity.ok(new ApiResponse(true,
                    "Resume uploaded and processed successfully", profile));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error uploading resume: " + e.getMessage()));
        }
    }
}