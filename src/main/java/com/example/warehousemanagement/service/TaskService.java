package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // 创建新任务
    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // 通过 ID 获取任务
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    // 获取所有任务
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 更新任务
    @Transactional
    public Task updateTask(Task task) {
        // 确保任务存在
        if (!taskRepository.existsById(task.getId())) {
            throw new NotFoundException("Task not found with id: " + task.getId());
        }
        return taskRepository.save(task);
    }

    // 开始任务
    @Transactional
    public Task startTask(Long id) {
        Task task = getTaskById(id);
        if (task.getStatus() != Task.TaskStatus.PENDING) {
            throw new IllegalStateException("只有待处理状态的任务可以开始");
        }
        task.setStatus(Task.TaskStatus.IN_PROGRESS);
        return taskRepository.save(task);
    }

    // 完成任务
    @Transactional
    public Task completeTask(Long id) {
        Task task = getTaskById(id);
        if (task.getStatus() != Task.TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中状态的任务可以完成");
        }
        task.setStatus(Task.TaskStatus.COMPLETED);
        return taskRepository.save(task);
    }

    // 删除任务
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // 根据状态获取任务
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    // 根据分配的用户获取任务
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignedUserId(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }
}