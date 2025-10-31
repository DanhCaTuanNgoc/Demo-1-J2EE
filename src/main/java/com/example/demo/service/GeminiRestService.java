package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Legacy REST API implementation - kept as backup
 * Now using Spring AI (GeminiAIService) as primary implementation
 */
@Service
public class GeminiRestService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String generateText(String userMessage) {
        if (!isEnabled()) {
            throw new IllegalStateException("Gemini REST is not enabled (missing API key)");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        // Simplified request schema for Gemini REST generateContent
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(Map.of("text", userMessage)));
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Build URL safely using UriComponentsBuilder
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("key", apiKey)
                .build()
                .toUriString();
        
        System.out.println("🔗 Requesting URL: " + url);
        System.out.println("📤 Request Body: " + requestBody);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        Map body = response.getBody();
        if (body == null) {
            return "";
        }

        // Extract the first candidate text safely
        try {
            List candidates = (List) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "";
            }
            Map first = (Map) candidates.get(0);
            Map content0 = (Map) first.get("content");
            List parts = (List) content0.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "";
            }
            Map part0 = (Map) parts.get(0);
            Object text = part0.get("text");
            return text == null ? "" : text.toString();
        } catch (Exception e) {
            return "";
        }
    }
}


