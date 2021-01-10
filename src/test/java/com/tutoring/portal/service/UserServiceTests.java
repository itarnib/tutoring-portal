package com.tutoring.portal.service;

import com.tutoring.portal.model.Role;
import com.tutoring.portal.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Tests if user is saved and password is encoded.
     */
    @Test
    void testSaveUser() {
        User user = createUser();
        User savedUser = userService.saveUser(user);

        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertNotEquals("password", savedUser.getPassword());
        assertTrue(bCryptPasswordEncoder.matches("password", savedUser.getPassword()));
    }

    /**
     * Tests getUserById method.
     */
    @Test
    void testGetUserById() {
        User savedUser = userService.saveUser(createUser());
        User foundUser = userService.getUserById(1);

        assertThat(foundUser)
            .returns(savedUser.getId(), from(User::getId))
            .returns(savedUser.getName(), from(User::getName))
            .returns(savedUser.getSurname(), from(User::getSurname))
            .returns(savedUser.getEmail(), from(User::getEmail))
            .returns(savedUser.getPassword(), from(User::getPassword));
    }

    /**
     * Tests findUserByEmail method.
     */
    @Test
    void testFindUserByEmail() {
        User savedUser = userService.saveUser(createUser());
        User foundUser = userService.findUserByEmail(savedUser.getEmail());

        assertThat(foundUser)
                .returns(savedUser.getId(), from(User::getId))
                .returns(savedUser.getName(), from(User::getName))
                .returns(savedUser.getSurname(), from(User::getSurname))
                .returns(savedUser.getEmail(), from(User::getEmail))
                .returns(savedUser.getPassword(), from(User::getPassword));
    }

    /**
     * Tests if user was updated.
     */
    @Test
    void testUpdateUser() {
        User savedUser = userService.saveUser(createUser());
        savedUser.setName("Kate");
        User updatedUser = userService.updateUser(savedUser);

        assertEquals("Kate", updatedUser.getName());
    }

    /**
     * Tests if user's password was updated and encoded.
     */
    @Test
    void testUpdatePassword() {
        User savedUser = userService.saveUser(createUser());
        savedUser.setPassword("newPassword");
        User updatedUser = userService.updatePassword(savedUser);

        assertNotEquals("newPassword", updatedUser.getPassword());
        assertTrue(bCryptPasswordEncoder.matches("newPassword", updatedUser.getPassword()));
    }

    /**
     * Tests if user was blocked.
     */
    @Test
    void testBlockUser() {
        User savedUser = userService.saveUser(createUser());
        assertEquals(1, savedUser.getActive());

        User blockedUser = userService.blockUser(savedUser);
        assertEquals(0, blockedUser.getActive());
    }

    /**
     * Tests if user was unblocked.
     */
    @Test
    void testUnblockUser() {
        User savedUser = userService.saveUser(createUser());
        savedUser.setActive(0);
        User blockedUser = userService.updateUser(savedUser);
        assertEquals(0, blockedUser.getActive());

        User unblockedUser = userService.unblockUser(savedUser);
        assertEquals(1, unblockedUser.getActive());
    }

    /**
     * Tests if ADMIN role was added.
     */
    @Test
    void testAddAdminRole() {
        User savedUser = userService.saveUser(createUser());
        User updatedUser = userService.addAdminRole(savedUser);

        assertTrue(updatedUser.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getRole())));
    }

    /**
     * Tests if ADMIN role was removed.
     */
    @Test
    void testRemoveAdminRole() {
        User user = createUser();
        Role role = new Role(1, "ADMIN");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        User savedUser = userService.saveUser(user);
        User updatedUser = userService.removeAdminRole(savedUser);

        assertTrue(updatedUser.getRoles().stream()
                .noneMatch(r -> "ADMIN".equals(r.getRole())));
    }

    /**
     * Tests if TUTOR role was added.
     */
    @Test
    void testAddTutorRole() {
        User savedUser = userService.saveUser(createUser());
        User updatedUser = userService.addTutorRole(savedUser);

        assertTrue(updatedUser.getRoles().stream()
                .anyMatch(r -> "TUTOR".equals(r.getRole())));
    }

    /**
     * Tests if TUTOR role was removed.
     */
    @Test
    void testRemoveTutorRole() {
        User user = createUser();
        Role role = new Role(1, "TUTOR");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        User savedUser = userService.saveUser(user);
        User updatedUser = userService.removeTutorRole(savedUser);

        assertTrue(updatedUser.getRoles().stream()
                .noneMatch(r -> "TUTOR".equals(r.getRole())));
    }

    /**
     * Tests user's deletion.
     */
    @Test
    void testDeleteUser() {
        User savedUser = userService.saveUser(createUser());
        int id = savedUser.getId();

        assertEquals(id, userService.deleteUser(id));
        assertNull(userService.getUserById(id));
    }

    /**
     * Tests getAllUsers method.
     */
    @Test
    void testGetAllUsers() {
        User user1 = createUser();
        userService.saveUser(user1);

        User user2 = createUser();
        user2.setId(2);
        user2.setName("Kate");
        userService.saveUser(user2);

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> "Kate".equals(user.getName())));
        assertTrue(users.stream().anyMatch(user -> "Mark".equals(user.getName())));
    }

    /**
     * Helper method for user creation.
     *
     * @return new user
     */
    public User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        user.setSurname("Smith");
        user.setPassword("password");
        user.setEmail("e@example.com");
        return user;
    }
}
