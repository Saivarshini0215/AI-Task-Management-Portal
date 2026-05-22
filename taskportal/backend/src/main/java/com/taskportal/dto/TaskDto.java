package com.taskportal.dto;

import com.taskportal.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Objects for task operations.
 */
public class TaskDto {

    /**
     * Request body for creating or updating a task.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskRequest {

        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
        private String title;

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        private String description;

        @NotNull(message = "Status is required")
        private Task.TaskStatus status;

        @NotNull(message = "Priority is required")
        private Task.Priority priority;

        private LocalDate dueDate;
    }

    /**
     * Response body for task data returned to the client.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private Task.TaskStatus status;
        private Task.Priority priority;
        private LocalDate dueDate;
        private String aiSummary;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String ownerEmail;
    }

    /**
     * Dashboard statistics for the current user.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskStats {
        private long total;
        private long todo;
        private long inProgress;
        private long done;
    }

    /**
     * Request body for AI summarization.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiSummarizeRequest {
        private String title;
        private String description;
    }

    /**
     * Response body from AI summarization.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiSummarizeResponse {
        private String summary;
    }
}
