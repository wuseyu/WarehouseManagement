package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.User;
import com.example.warehousemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.createUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testUser");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(1L);
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByUsername() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        User foundUser = userService.getUserByUsername("testUser");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testUser");

        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUsername("user2");

        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> allUsers = userService.getAllUsers();

        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(2);
        assertThat(allUsers.get(0).getUsername()).isEqualTo("user1");
        assertThat(allUsers.get(1).getUsername()).isEqualTo("user2");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
