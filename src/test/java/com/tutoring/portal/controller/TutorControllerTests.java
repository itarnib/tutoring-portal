package com.tutoring.portal.controller;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.model.Role;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TutorControllerTests {

    @InjectMocks
    TutorController tutorController;

    @Mock
    UserService userService;

    @Mock
    UserAuthentication userAuthentication;

    @Mock
    Model model;

    @Mock
    BindingResult result;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests getAllTutors method.
     */
    @Test
    void testGetAllTutors() {
        when(userService.getAllUsersWithTutorRole()).thenReturn(null);
        assertEquals("tutors", tutorController.getAllTutors(model));
    }

    /**
     * Tests getTutorConsultations method.
     */
    @Test
    void testGetTutorConsultations() {
        User user = createUser();
        when(userService.getUserById(1)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        assertEquals("consultations", tutorController.getTutorConsultations(1, model));
    }

    /**
     * Tests getTutorConsultations method with null tutor.
     */
    @Test
    void testGetTutorConsultationsWithNullTutor() {
        when(userService.getUserById(1)).thenReturn(null);
        assertEquals("errors/error-404", tutorController.getTutorConsultations(1, model));
    }

    /**
     * Tests getTutorConsultations method with invalid tutor (without tutor role).
     */
    @Test
    void testGetTutorConsultationsWithInvalidTutor() {
        User user = createUser();
        user.setRoles(new HashSet<>());
        when(userService.getUserById(1)).thenReturn(user);
        assertEquals("errors/error-404", tutorController.getTutorConsultations(1, model));
    }

    /**
     * Tests getTutorComments method.
     */
    @Test
    void testGetTutorComments() {
        User user = createUser();
        when(userService.getUserById(1)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        assertEquals("comments", tutorController.getTutorComments(1, new Comment(), model));
    }

    /**
     * Tests getTutorComments method with null tutor.
     */
    @Test
    void testGetTutorCommentsWithNullTutor() {
        when(userService.getUserById(1)).thenReturn(null);
        assertEquals("errors/error-404", tutorController.getTutorComments(1, new Comment(), model));
    }

    /**
     * Tests getTutorComments method with invalid tutor (without tutor role).
     */
    @Test
    void testGetTutorCommentsWithInvalidTutor() {
        User user = createUser();
        user.setRoles(new HashSet<>());
        when(userService.getUserById(1)).thenReturn(user);
        assertEquals("errors/error-404", tutorController.getTutorComments(1, new Comment(), model));
    }

    /**
     * Tests addComment method.
     */
    @Test
    void testAddComment() {
        User user = createUser();
        user.setId(2);
        when(userService.getUserById(2)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("redirect:/tutors/2/comments", tutorController.addComment(2, new Comment(), result, model));
    }

    /**
     * Tests addComment method with null tutor.
     */
    @Test
    void testAddCommentWithNullTutor() {
        when(userService.getUserById(1)).thenReturn(null);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-404", tutorController.addComment(1, new Comment(), result, model));
    }

    /**
     * Tests addComment method with invalid tutor (without tutor role).
     */
    @Test
    void testAddCommentWithInvalidTutor() {
        User user = createUser();
        user.setRoles(new HashSet<>());
        when(userService.getUserById(1)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        assertEquals("errors/error-404", tutorController.addComment(1, new Comment(), result, model));
    }

    /**
     * Tests addComment method with error-403 (user trying to add comment about himself).
     */
    @Test
    void testAddCommentWithError403() {
        User user = createUser();
        when(userService.getUserById(1)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        assertEquals("errors/error-403", tutorController.addComment(1, new Comment(), result, model));
    }

    /**
     * Tests addComment method with binding error.
     */
    @Test
    void testAddCommentWithBindingError() {
        User user = createUser();
        user.setId(2);
        when(userService.getUserById(2)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        when(result.hasErrors()).thenReturn(true);
        assertEquals("comments", tutorController.addComment(2, new Comment(), result, model));
    }


    /**
     * Helper method for user creation.
     * @return new user
     */
    public User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        user.setSurname("Smith");
        user.setPassword("password");
        user.setEmail("e@example.com");
        Role role = new Role(1, "TUTOR");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        user.setCreatedConsultations(new HashSet<>());
        return user;
    }
}