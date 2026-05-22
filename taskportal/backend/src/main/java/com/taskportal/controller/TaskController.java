package com.taskportal.controller;

import com.taskportal.dto.TaskDto;
import com.taskportal.entity.User;
import com.taskportal.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    
    @GetMapping
    public ResponseEntity<List<TaskDto.TaskResponse>> getAllTasks(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(taskService.getAllTasks(user));
    }

    /**
     * Get a specific task by ID.
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto.TaskResponse> getTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(taskService.getTask(id, user));
    }

    /**
     * Create a new task.
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskDto.TaskResponse> createTask(
            @Valid @RequestBody TaskDto.TaskRequest request,
            @AuthenticationPrincipal User user
    ) {
        TaskDto.TaskResponse created = taskService.createTask(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing task.
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto.TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto.TaskRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    /**
     * Delete a task.
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Generate an AI summary for a task.
     * POST /api/tasks/{id}/ai-summarize
     */
    @PostMapping("/{id}/ai-summarize")
    public ResponseEntity<TaskDto.TaskResponse> generateAiSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(taskService.generateAiSummary(id, user));
    }

    /**
     * Get task statistics for the authenticated user.
     * GET /api/tasks/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<TaskDto.TaskStats> getStats(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(taskService.getStats(user));
    }
}
