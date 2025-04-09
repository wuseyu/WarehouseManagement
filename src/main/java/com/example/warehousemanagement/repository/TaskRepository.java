package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 根据任务状态查找任务
    List<Task> findByStatus(Task.TaskStatus status);

    // 根据分配的用户查找任务
    List<Task> findByAssignedUserId(Long userId);
} 