package com.tutoring.portal.controller;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.CommentService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserControllerTests {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    @Mock
    CommentService commentService;

    @Mock
    UserAuthentication userAuthentication;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    Model model;

    @Mock
    BindingResult result;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests login method.
     */
    @Test
    void testLogin() {
        assertEquals("login", userController.login());
    }

    /**
     * Tests home method.
     */
    @Test
    void testHome() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("index", userController.home(model));
    }

    /**
     * Tests registration method.
     */
    @Test
    void testRegistration() {
        assertEquals("registration", userController.registration(createUser()));
    }

    /**
     * Tests createNewUser method.
     */
    @Test
    void testCreateNewUser() {
        User user = createUser();
        when(userService.saveUser(user)).thenReturn(user);
        assertEquals("registration", userController.createNewUser(user, result, model));
    }

    /**
     * Tests myComments method.
     */
    @Test
    void testMyComments() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("my-comments", userController.myComments(model));
    }

    /**
     * Tests myCommentsUpdate method.
     */
    @Test
    void testMyCommentsUpdate() {
        when(commentService.getCommentById(1)).thenReturn(createComment());
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("update-comment", userController.myCommentsUpdate(1, model));
    }

    /**
     * Tests myCommentsUpdate method with error-404 view.
     */
    @Test
    void testMyCommentsUpdateWithError404() {
        when(commentService.getCommentById(1)).thenReturn(null);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-404", userController.myCommentsUpdate(1, model));
    }

    /**
     * Tests myCommentsUpdate method with error-403 view.
     */
    @Test
    void testMyCommentsUpdateWithError403() {
        Comment comment = createComment();
        comment.getStudent().setId(2);
        when(commentService.getCommentById(1)).thenReturn(comment);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-403", userController.myCommentsUpdate(1, model));
    }

    /**
     * Tests saveUpdatedComment method.
     */
    @Test
    void testSaveUpdatedComment() {
        Comment comment = createComment();
        when(commentService.getCommentById(1)).thenReturn(comment);
        when(commentService.saveComment(comment)).thenReturn(comment);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("redirect:/my-comments", userController.saveUpdatedComment(1, comment, result, model));
    }

    /**
     * Tests saveUpdatedComment method with binding errors.
     */
    @Test
    void testSaveUpdatedCommentWithBindingErrors() {
        Comment comment = createComment();
        when(commentService.getCommentById(1)).thenReturn(comment);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        when(result.hasErrors()).thenReturn(true);
        assertEquals("update-comment", userController.saveUpdatedComment(1, comment, result, model));
    }

    /**
     * Tests saveUpdatedComment method with error-404.
     */
    @Test
    void testSaveUpdatedCommentWithError404() {
        when(commentService.getCommentById(1)).thenReturn(null);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-404", userController.saveUpdatedComment(1, createComment(), result, model));
    }

    /**
     * Tests saveUpdatedComment method with error-403.
     */
    @Test
    void testSaveUpdatedCommentWithError403() {
        Comment comment = createComment();
        comment.getStudent().setId(2);
        when(commentService.getCommentById(1)).thenReturn(comment);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-403", userController.saveUpdatedComment(1, comment, result, model));
    }

    /**
     * Tests myCommentsDelete method.
     */
    @Test
    void testMyCommentsDelete() {
        when(commentService.getCommentById(1)).thenReturn(createComment());
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("redirect:/my-comments", userController.myCommentsDelete(1, model));
    }

    /**
     * Tests myCommentsDelete method with error-404.
     */
    @Test
    void testMyCommentsDeleteWithError404() {
        when(commentService.getCommentById(1)).thenReturn(null);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-404", userController.myCommentsDelete(1, model));
    }

    /**
     * Tests myCommentsDelete method with error-403.
     */
    @Test
    void testMyCommentsDeleteWithError403() {
        Comment comment = createComment();
        comment.getStudent().setId(2);
        when(commentService.getCommentById(1)).thenReturn(comment);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-403", userController.myCommentsDelete(1, model));
    }

    /**
     * Tests updateProfile method.
     */
    @Test
    void testUpdateProfile() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("update-user", userController.updateProfile(model));
    }

    /**
     * Tests saveUpdatedProfile method.
     */
    @Test
    void testSaveUpdatedProfile() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        assertEquals("update-user", userController.saveUpdatedProfile(user, result, model));
    }

    /**
     * Tests saveUpdatedProfile method with binding errors.
     */
    @Test
    void testSaveUpdatedProfileWithBindingErrors() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(result.hasErrors()).thenReturn(true);
        assertEquals("update-user", userController.saveUpdatedProfile(user, result, model));
    }

    /**
     * Tests updatePassword method.
     */
    @Test
    void testUpdatePassword() {
        assertEquals("update-password", userController.updatePassword());
    }

    /**
     * Tests saveUpdatedPassword method.
     */
    @Test
    void testSaveUpdatedPassword() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password", user.getPassword())).thenReturn(true);
        when(bCryptPasswordEncoder.matches("newPassword", user.getPassword())).thenReturn(false);
        assertEquals("update-password", userController.saveUpdatedPassword("password", "newPassword", model));
    }

    /**
     * Tests saveUpdatedPassword method with old password error.
     */
    @Test
    void testSaveUpdatedPasswordWithOldPasswordError() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password1", user.getPassword())).thenReturn(false);
        assertEquals("update-password", userController.saveUpdatedPassword("password1", "newPassword", model));
    }

    /**
     * Tests saveUpdatedPassword method with new password error.
     */
    @Test
    void testSaveUpdatedPasswordWithNewPasswordError() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password", user.getPassword())).thenReturn(true);
        when(bCryptPasswordEncoder.matches("password", user.getPassword())).thenReturn(true);
        assertEquals("update-password", userController.saveUpdatedPassword("password", "password", model));
    }

    /**
     * Tests saveUpdatedPassword method with short new password error.
     */
    @Test
    void testSaveUpdatedPasswordWithShortNewPasswordError() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password", user.getPassword())).thenReturn(true);
        when(bCryptPasswordEncoder.matches("pass", user.getPassword())).thenReturn(false);
        assertEquals("update-password", userController.saveUpdatedPassword("password", "pass", model));
    }

    /**
     * Tests deleteProfile method.
     */
    @Test
    void testDeleteProfile() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("redirect:/logout", userController.deleteProfile());
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
        return user;
    }

    /**
     * Helper method for comment creation.
     * @return new comment
     */
    public Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setFeedback("good tutor");
        comment.setStudent(createUser());
        return comment;
    }
}
