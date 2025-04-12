package com.example.warehousemanagement.repository;

import com.example.warehousemanagement.entity.Role;
import com.example.warehousemanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

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

    @Autowired
    private RoleRepository roleRepository;

    private Role createRole(String name, String responsibility) {
        Role role = new Role();
        role.setName(name);
        role.setType(name.replace("ROLE_", ""));
        role.setResponsibility(responsibility);
        return roleRepository.save(role); // 先保存 Role
    }


    @Test
    public void testSaveAndFindUser() {
        Role role = createRole("ROLE_SUPER_ADMIN", "系统最高权限");
        User savedUser = createUser("testUser", "password123", "test@example.com", role);

        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testFindByUsername() {
        createUser("findUser", "securePass", "find@example.com", null);
        Optional<User> foundUserOptional = userRepository.findByUsername("findUser");

        assertThat(foundUserOptional).isPresent();
        assertThat(foundUserOptional.get().getUsername()).isEqualTo("findUser");
    }

    @Test
    public void testDeleteUser() {
        User user = createUser("deleteUser", "deletePass", "delete@example.com", null);
        userRepository.deleteById(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    public void testUpdateUser() {
        User user = createUser("updateUser", "oldPass", "update@example.com", null);
        user.setPassword("newPass");
        user.setEmail("new@example.com");
        userRepository.save(user);

        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo("newPass");
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }
/**
 * Todo: add test for FindUsersByRoleName
 */
//    @Test
//    public void testFindUsersByRoleName() {
//        Role adminRole = createRole("ROLE_SUPER_ADMIN", "系统最高权限");
//        User user1 = createUser("adminUser1", "adminPass1", "admin1@example.com", adminRole);
//        User user2 = createUser("adminUser2", "adminPass2", "admin2@example.com", adminRole);
//        User user3 = createUser("regularUser", "userPass", "user@example.com", null);
//
//        List<User> adminUsers = userRepository.findUsersByRoleName("ROLE_SUPER_ADMIN");
//
//        assertThat(adminUsers).isNotEmpty();
//        assertThat(adminUsers).contains(user1, user2);
//        assertThat(adminUsers).doesNotContain(user3);
//    }

} 