package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 创建任务
    @PostMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('TASK_CREATE')")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskService.createTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    // 根据 ID 查找任务
    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('TASK_VIEW')")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    // 获取所有任务
    @GetMapping
    @PreAuthorize("@customSecurityExpression.hasPermission('TASK_VIEW')")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // 删除任务
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.hasPermission('TASK_DELETE')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 根据状态获取任务
    @GetMapping("/status/{status}")
    @PreAuthorize("@customSecurityExpression.hasPermission('TASK_VIEW')")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // 根据分配的用户获取任务
    @GetMapping("/user/{userId}")
    @PreAuthorize("@customSecurityExpression.hasPermission('TASK_VIEW')")
    public ResponseEntity<List<Task>> getTasksByAssignedUserId(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByAssignedUserId(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // 异常处理
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}