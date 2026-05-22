package com.taskportal.repository;

import com.taskportal.entity.Task;
import com.taskportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity.
 * Provides filtered queries scoped to the authenticated user.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Get all tasks belonging to a specific user, ordered by creation date descending.
     */
    List<Task> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find a task by ID and ensure it belongs to the specified user (security check).
     */
    Optional<Task> findByIdAndUser(Long id, User user);

    /**
     * Count tasks by status for a specific user (used for dashboard stats).
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Task.TaskStatus status);

    /**
     * Find tasks by status for a specific user.
     */
    List<Task> findByUserAndStatusOrderByCreatedAtDesc(User user, Task.TaskStatus status);
}
