package com.taskportal.service;

import com.taskportal.dto.TaskDto;
import com.taskportal.entity.Task;
import com.taskportal.entity.User;
import com.taskportal.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.taskportal.exception.GlobalExceptionHandler.UnauthorizedException;
import com.taskportal.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for all task management operations.
 * Enforces user-scoped access control on every operation.
 */
@Service
@Transactional
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final AiService aiService;

    public TaskService(TaskRepository taskRepository, AiService aiService) {
        this.taskRepository = taskRepository;
        this.aiService = aiService;
    }

    /**
     * Get all tasks for the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<TaskDto.TaskResponse> getAllTasks(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get a specific task by ID, ensuring it belongs to the current user.
     */
    @Transactional(readOnly = true)
    public TaskDto.TaskResponse getTask(Long id, User user) {
        Task task = findTaskForUser(id, user);
        return mapToResponse(task);
    }

    /**
     * Create a new task for the current user.
     */
    public TaskDto.TaskResponse createTask(TaskDto.TaskRequest request, User user) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .user(user)
                .build();

        Task saved = taskRepository.save(task);
        logger.info("Created task '{}' for user {}", saved.getTitle(), user.getEmail());
        return mapToResponse(saved);
    }

    /**
     * Update an existing task, ensuring it belongs to the current user.
     */
    public TaskDto.TaskResponse updateTask(Long id, TaskDto.TaskRequest request, User user) {
        Task task = findTaskForUser(id, user);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task saved = taskRepository.save(task);
        logger.info("Updated task '{}' for user {}", saved.getId(), user.getEmail());
        return mapToResponse(saved);
    }

    /**
     * Delete a task, ensuring it belongs to the current user.
     */
    public void deleteTask(Long id, User user) {
        Task task = findTaskForUser(id, user);
        taskRepository.delete(task);
        logger.info("Deleted task {} for user {}", id, user.getEmail());
    }

    /**
     * Generate an AI summary for a task and persist it.
     */
    public TaskDto.TaskResponse generateAiSummary(Long id, User user) {
        Task task = findTaskForUser(id, user);
        String summary = aiService.summarizeTask(task.getTitle(), task.getDescription());
        task.setAiSummary(summary);
        return mapToResponse(taskRepository.save(task));
    }

    /**
     * Get aggregated statistics for the current user's tasks.
     */
    @Transactional(readOnly = true)
    public TaskDto.TaskStats getStats(User user) {
        long total = taskRepository.countByUserAndStatus(user, Task.TaskStatus.TODO)
                + taskRepository.countByUserAndStatus(user, Task.TaskStatus.IN_PROGRESS)
                + taskRepository.countByUserAndStatus(user, Task.TaskStatus.DONE);

        return TaskDto.TaskStats.builder()
                .total(total)
                .todo(taskRepository.countByUserAndStatus(user, Task.TaskStatus.TODO))
                .inProgress(taskRepository.countByUserAndStatus(user, Task.TaskStatus.IN_PROGRESS))
                .done(taskRepository.countByUserAndStatus(user, Task.TaskStatus.DONE))
                .build();
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────────

    /**
     * Find a task by ID and verify it belongs to the user. Throws appropriate exceptions otherwise.
     */
    private Task findTaskForUser(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> {
                    // Check if the task exists at all (for better error messages)
                    if (taskRepository.existsById(id)) {
                        return new UnauthorizedException("You don't have permission to access this task");
                    }
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });
    }

    /**
     * Map a Task entity to a TaskResponse DTO.
     */
    private TaskDto.TaskResponse mapToResponse(Task task) {
        return TaskDto.TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .aiSummary(task.getAiSummary())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .ownerEmail(task.getUser().getEmail())
                .build();
    }
}
