package org.synlabs.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String name;
    private String userType;

    public JwtResponse(String token, Long id, String email, String name, String userType) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
        this.userType = userType;
    }
}