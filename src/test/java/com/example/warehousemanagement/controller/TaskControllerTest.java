package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    private TaskController taskController;
    private Task testTask;

    @BeforeEach
    void setup() {
        taskController = new TaskController(taskService);
        
        testTask = new Task();
        testTask.setId(1L);
        testTask.setDescription("Test Task");
        testTask.setScheduledTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    @WithMockUser(authorities = "TASK_VIEW")
    void getTaskById_ShouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(testTask);

        ResponseEntity<Task> response = taskController.getTaskById(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Task", response.getBody().getDescription());
    }

    @Test
    @WithMockUser(authorities = "TASK_VIEW")
    void getTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new NotFoundException("Task not found"));

        assertThrows(NotFoundException.class, () -> taskController.getTaskById(1L));
    }

    @Test
    @WithMockUser(authorities = "TASK_VIEW")
    void getAllTasks_ShouldReturnTaskList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.singletonList(testTask));

        ResponseEntity<List<Task>> response = taskController.getAllTasks();
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getDescription());
    }

    @Test
    @WithMockUser(authorities = "TASK_VIEW")
    void getTasksByStatus_ShouldReturnTaskList() throws Exception {
        when(taskService.getTasksByStatus(Task.TaskStatus.PENDING)).thenReturn(Collections.singletonList(testTask));

        ResponseEntity<List<Task>> response = taskController.getTasksByStatus(Task.TaskStatus.PENDING);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getDescription());
    }

    @Test
    @WithMockUser(authorities = "TASK_VIEW")
    void getTasksByAssignedUserId_ShouldReturnTaskList() throws Exception {
        when(taskService.getTasksByAssignedUserId(1L)).thenReturn(Collections.singletonList(testTask));

        ResponseEntity<List<Task>> response = taskController.getTasksByAssignedUserId(1L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getDescription());
    }
}