package org.synlabs.assignment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest{
    @NotBlank(message = "name is required")
    private String name;
    @Email(message = "email should be valid")
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;
    @NotBlank(message = "user type is required")
    private String userType;
    @NotBlank(message = "profile headline is required")
    private String profileHeadline;
    @NotBlank(message = "Address is required")
    private String address;
}
