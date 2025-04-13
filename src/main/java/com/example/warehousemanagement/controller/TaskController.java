package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.entity.Vehicle;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.TaskService;
import com.example.warehousemanagement.service.UserService;
import com.example.warehousemanagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STORE')")  // 超级管理员、管理员和门店可以访问任务相关接口
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final VehicleService vehicleService;

    /**
     * 创建任务
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> taskData) {
        Task newTask = new Task();
        
        // 设置基本信息
        if (taskData.containsKey("description")) {
            newTask.setDescription((String) taskData.get("description"));
        }
        
        if (taskData.containsKey("status") && taskData.get("status") != null) {
            newTask.setStatus(Task.TaskStatus.valueOf((String) taskData.get("status")));
        } else {
            // 默认状态为PENDING
            newTask.setStatus(Task.TaskStatus.PENDING);
        }
        
        if (taskData.containsKey("destination")) {
            newTask.setDestination((String) taskData.get("destination"));
        }
        
        if (taskData.containsKey("scheduledTime") && taskData.get("scheduledTime") != null) {
            newTask.setScheduledTime(LocalDateTime.parse((String) taskData.get("scheduledTime")));
        }
        
        // 设置关联对象
        if (taskData.containsKey("assignedUserId") && taskData.get("assignedUserId") != null) {
            Long userId = Long.valueOf(taskData.get("assignedUserId").toString());
            User user = userService.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
            newTask.setAssignedUser(user);
        }
        
        if (taskData.containsKey("vehicleId") && taskData.get("vehicleId") != null) {
            Long vehicleId = Long.valueOf(taskData.get("vehicleId").toString());
            Vehicle vehicle = vehicleService.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));
            newTask.setVehicle(vehicle);
        }
        
        Task savedTask = taskService.createTask(newTask);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    /**
     * 根据 ID 查找任务
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    /**
     * 获取所有任务
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * 更新任务 - 使用部分更新方式避免序列化问题
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Task existingTask = taskService.getTaskById(id);
        
        // 更新任务基本信息
        if (updates.containsKey("description")) {
            existingTask.setDescription((String) updates.get("description"));
        }
        
        if (updates.containsKey("status") && updates.get("status") != null) {
            existingTask.setStatus(Task.TaskStatus.valueOf((String) updates.get("status")));
        }
        
        if (updates.containsKey("destination")) {
            existingTask.setDestination((String) updates.get("destination"));
        }
        
        if (updates.containsKey("scheduledTime") && updates.get("scheduledTime") != null) {
            existingTask.setScheduledTime(LocalDateTime.parse((String) updates.get("scheduledTime")));
        }
        
        // 更新关联对象
        if (updates.containsKey("assignedUserId") && updates.get("assignedUserId") != null) {
            Long userId = Long.valueOf(updates.get("assignedUserId").toString());
            User user = userService.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
            existingTask.setAssignedUser(user);
        }
        
        if (updates.containsKey("vehicleId") && updates.get("vehicleId") != null) {
            Long vehicleId = Long.valueOf(updates.get("vehicleId").toString());
            Vehicle vehicle = vehicleService.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));
            existingTask.setVehicle(vehicle);
        }
        
        Task updatedTask = taskService.updateTask(existingTask);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    /**
     * 开始任务
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<Task> startTask(@PathVariable Long id) {
        Task task = taskService.startTask(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    /**
     * 完成任务
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        Task task = taskService.completeTask(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据状态获取任务
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * 根据分配的用户获取任务
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignedUserId(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByAssignedUserId(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // 异常处理
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}