package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.exception.NotFoundException;
import com.example.warehousemanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    private User user;
    private Task task;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        task = new Task();
        task.setId(1L);
        task.setDescription("Test Task");
        task.setAssignedUser(user);
        task.setScheduledTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getDescription()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(1L);

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(1L);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetTaskByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class, () -> {
            taskService.getTaskById(1L);
        });

        assertThat(exception.getMessage()).isEqualTo("Task not found");
    }

    @Test
    public void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<Task> tasks = taskService.getAllTasks();

        assertThat(tasks).isNotEmpty();
        assertThat(tasks.size()).isEqualTo(1);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteTask() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetTasksByStatus() {
        when(taskRepository.findByStatus(Task.TaskStatus.PENDING)).thenReturn(Arrays.asList(task));

        List<Task> tasks = taskService.getTasksByStatus(Task.TaskStatus.PENDING);

        assertThat(tasks).isNotEmpty();
        assertThat(tasks.size()).isEqualTo(1);
        verify(taskRepository, times(1)).findByStatus(Task.TaskStatus.PENDING);
    }

    @Test
    public void testGetTasksByAssignedUserId() {
        when(taskRepository.findByAssignedUserId(1L)).thenReturn(Arrays.asList(task));

        List<Task> tasks = taskService.getTasksByAssignedUserId(1L);

        assertThat(tasks).isNotEmpty();
        assertThat(tasks.size()).isEqualTo(1);
        verify(taskRepository, times(1)).findByAssignedUserId(1L);
    }
} 