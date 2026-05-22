package com.taskportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taskportal.exception.GlobalExceptionHandler.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service for AI-powered task operations using the Anthropic Claude API.
 * Provides task summarization and smart suggestions.
 */
@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Generate an AI summary for a task given its title and description.
     *
     * @param title       Task title
     * @param description Task description (may be empty)
     * @return A concise, actionable AI-generated summary
     */
    public String summarizeTask(String title, String description) {
        String prompt = buildSummarizationPrompt(title, description);

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("max_tokens", 300);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.set("messages", messages);

            String responseBody = webClient.post()
                    .uri(apiUrl)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseAiResponse(responseBody);

        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("AI API call failed: {}", e.getMessage());
            throw new AiServiceException("Failed to connect to AI service: " + e.getMessage());
        }
    }

    /**
     * Parse the Anthropic API response and extract the text content.
     */
    private String parseAiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            // Check for API errors
            if (root.has("error")) {
                String errorMsg = root.get("error").get("message").asText();
                throw new AiServiceException("AI API error: " + errorMsg);
            }

            // Extract text from content array
            JsonNode content = root.get("content");
            if (content != null && content.isArray() && content.size() > 0) {
                return content.get(0).get("text").asText().trim();
            }

            throw new AiServiceException("Unexpected response format from AI service");
        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to parse AI response: {}", e.getMessage());
            throw new AiServiceException("Failed to parse AI response");
        }
    }

    /**
     * Build a structured prompt for task summarization.
     */
    private String buildSummarizationPrompt(String title, String description) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a productivity assistant. Analyze the following task and provide a concise, ");
        prompt.append("actionable summary in 2-3 sentences. Include key points, potential subtasks, and any ");
        prompt.append("important considerations. Be specific and practical.\n\n");
        prompt.append("Task Title: ").append(title).append("\n");

        if (description != null && !description.isBlank()) {
            prompt.append("Task Description: ").append(description).append("\n");
        } else {
            prompt.append("Task Description: (No description provided)\n");
        }

        prompt.append("\nProvide only the summary, no preamble or extra commentary.");
        return prompt.toString();
    }
}
