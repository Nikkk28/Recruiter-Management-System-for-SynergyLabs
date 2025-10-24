package org.synlabs.assignment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.synlabs.assignment.dto.ApiResponse;
import org.synlabs.assignment.dto.JwtResponse;
import org.synlabs.assignment.dto.LoginRequest;
import org.synlabs.assignment.dto.SignupRequest;
import org.synlabs.assignment.model.User;
import org.synlabs.assignment.model.enums.UserType;
import org.synlabs.assignment.repository.UserRepository;
import org.synlabs.assignment.security.JwtUtil;
import org.synlabs.assignment.security.UserDetailsImpl;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email is already taken!"));
            }

            UserType userType;
            try {
                userType = UserType.valueOf(signUpRequest.getUserType().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid user type. Must be ADMIN or APPLICANT"));
            }

            User user = new User();
            user.setName(signUpRequest.getName());
            user.setEmail(signUpRequest.getEmail());
            user.setAddress(signUpRequest.getAddress());
            user.setUserType(userType);
            user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setProfileHeadline(signUpRequest.getProfileHeadline());

            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            JwtResponse response = new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    user.getName(),
                    userDetails.getUserType().name()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid email or password"));
        }
    }
}