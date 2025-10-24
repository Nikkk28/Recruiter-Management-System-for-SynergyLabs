package org.synlabs.assignment.service;

import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.synlabs.assignment.dto.ResumeParserResponse;

import java.util.stream.Collectors;

@Service
public class ResumeParserService {

    @Value("${resume.parser.api.url}")
    private String apiUrl;

    @Value("${resume.parser.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public ResumeParserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResumeParserResponse parseResume(byte[] fileBytes) {
        try {
            ResumeParserResponse response = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header("apikey", apiKey)
                    .body(BodyInserters.fromValue(fileBytes))
                    .retrieve()
                    .bodyToMono(ResumeParserResponse.class)
                    .block();

            return response;
        } catch (Exception e) {
            System.err.println("Error parsing resume: " + e.getMessage());
            return null;
        }
    }

    public String formatSkills(ResumeParserResponse response) {
        if (response.getSkills() == null || response.getSkills().isEmpty()) {
            return null;
        }
        return String.join(", ", response.getSkills());
    }

    public String formatEducation(ResumeParserResponse response) {
        if (response.getEducation() == null || response.getEducation().isEmpty()) {
            return null;
        }
        return response.getEducation().stream()
                .map(edu -> edu.getName())
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.joining("; "));
    }

    public String formatExperience(ResumeParserResponse response) {
        if (response.getExperience() == null || response.getExperience().isEmpty()) {
            return null;
        }
        return response.getExperience().stream()
                .map(exp -> {
                    String dates = exp.getDates() != null && !exp.getDates().isEmpty()
                            ? "(" + String.join(", ", exp.getDates()) + ")"
                            : "";
                    return exp.getName() + " " + dates;
                })
                .filter(exp -> exp != null && !exp.trim().isEmpty())
                .collect(Collectors.joining("; "));
    }
}