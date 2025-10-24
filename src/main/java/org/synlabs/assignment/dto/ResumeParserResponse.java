package org.synlabs.assignment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeParserResponse {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private List<Education> education;
    private List<Experience> experience;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Education {
        private String name;
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Experience {
        private String name;
        private String url;
        private List<String> dates;
    }
}