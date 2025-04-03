package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.Task;
import com.example.warehousemanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.warehousemanagement.entity.Task.TaskStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username, String password, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        if (role != null) {
            user.getRoles().add(role);
        }
        return userRepository.save(user);
    }

    private Task createTask(String description, Task.TaskStatus status, User assignedUser, String destination  ) {
        Task task = new Task();
        task.setDescription(description);
        task.setStatus(status);
        task.setAssignedUser(assignedUser);
        task.setDestination(destination);
        task.setScheduledTime(LocalDateTime.now().plusDays(1)); // 设置计划执行时间
        return taskRepository.save(task);
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    public void testFindByStatus() {
        User user = createUser("testUser1", "testPass1", "test1@example.com", null);
        createTask("Test Task 1", PENDING, user, "Test Destination");
        createTask("Test Task 2", Task.TaskStatus.COMPLETED, user, "Test Destination");

        List<Task> pendingTasks = taskRepository.findByStatus(PENDING);

        assertThat(pendingTasks).hasSize(1);
        assertThat(pendingTasks.get(0).getDescription()).isEqualTo("Test Task 1");
    }

    @Test
    public void testFindByAssignedUserId() {
        User user = createUser("testUser2", "testPass2", "test2@example.com", null);
        Task task1 = createTask("Task for User", PENDING, user, "Test Destination");
        createTask("Another Task", PENDING, null,"Test Destination"); // 不分配用户

        List<Task> userTasks = taskRepository.findByAssignedUserId(user.getId());

        assertThat(userTasks).hasSize(1);
        assertThat(userTasks.get(0).getDescription()).isEqualTo(task1.getDescription());
    }
} 