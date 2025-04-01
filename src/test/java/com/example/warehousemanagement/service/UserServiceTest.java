package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.warehousemanagement.entity.Role.RoleType.SUPER_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("test@example.com");

        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testUser");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testFindByUsername() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        User foundUser = userService.findByUsername(username);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(username);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testFindUsersByRoleName() {
        String roleName = "SUPER_ADMIN";
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        userList.add(user1);
        userList.add(user2);

        when(userRepository.findUsersByRoleName(roleName)).thenReturn(userList);

        List<User> foundUsers = userService.findUsersByRoleName(roleName);

        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.size()).isEqualTo(2);
        verify(userRepository, times(1)).findUsersByRoleName(roleName);
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(id);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(id);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void testDeleteUser() {
        Long id = 1L;

        doNothing().when(userRepository).deleteById(id);

        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void testFindAllUsers() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        userList.add(user1);
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> allUsers = userService.findAllUsers();

        assertThat(allUsers).isNotEmpty();
        assertThat(allUsers.size()).isEqualTo(2);
        verify(userRepository, times(1)).findAll();
    }
}