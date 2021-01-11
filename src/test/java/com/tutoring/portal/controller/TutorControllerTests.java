package com.tutoring.portal.controller;

import com.tutoring.portal.service.CommentService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TutorControllerTests {

    @InjectMocks
    TutorController tutorController;

    @Mock
    UserService userService;

    @Mock
    CommentService commentService;

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

    @Test
    void testGetAllTutors() {
        when(userService.getAllUsersWithTutorRole()).thenReturn(null);
        assertEquals("tutors", tutorController.getAllTutors(model));
    }

    @Test
    void testGetTutorConsultations() {
        when(userService.getAllUsersWithTutorRole()).thenReturn(null);
        assertEquals("tutors", tutorController.getAllTutors(model));
    }
}