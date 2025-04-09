package com.example.warehousemanagement.controller;

import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setup() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setDescription("Test Task");
        testTask.setScheduledTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void createTask_ShouldReturnCreated() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test Task\",\"scheduledTime\":\"2025-04-04T10:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Task"));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(testTask);

        mockMvc.perform(get("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Task"));
    }


    @Test
    void getTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.singletonList(testTask));

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Task"));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTasksByStatus_ShouldReturnTaskList() throws Exception {
        when(taskService.getTasksByStatus(Task.TaskStatus.PENDING)).thenReturn(Collections.singletonList(testTask));

        mockMvc.perform(get("/api/tasks/status/{status}", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Task"));
    }

    @Test
    void getTasksByAssignedUserId_ShouldReturnTaskList() throws Exception {
        when(taskService.getTasksByAssignedUserId(1L)).thenReturn(Collections.singletonList(testTask));

        mockMvc.perform(get("/api/tasks/user/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Task"));
    }
}