package com.tutoring.portal.controller;

import com.tutoring.portal.model.Role;
import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.ConsultationService;
import com.tutoring.portal.service.SubjectService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SubjectControllerTests {

    @InjectMocks
    SubjectController subjectController;

    @Mock
    SubjectService subjectService;

    @Mock
    UserService userService;

    @Mock
    ConsultationService consultationService;

    @Mock
    UserAuthentication userAuthentication;

    @Mock
    Model model;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests getAllSubjects method.
     */
    @Test
    void testGetAllSubjects() {
        when(subjectService.getAllSubjects()).thenReturn(new ArrayList<>());
        assertEquals("subjects", subjectController.getAllSubjects(model));
    }

    /**
     * Tests getAllTutorsBySubject method.
     */
    @Test
    void testGetAllTutorsBySubject() {
        when(subjectService.getSubjectById(1)).thenReturn(createSubject());
        when(userService.getAllUsersWithTutorRole()).thenReturn(new ArrayList<>(Collections.singletonList(createUser())));
        assertEquals("tutors", subjectController.getAllTutorsBySubject(1, model));
    }

    /**
     * Tests getAllTutorsBySubject method with null subject.
     */
    @Test
    void testGetAllTutorsBySubjectWithNullSubject() {
        when(subjectService.getSubjectById(1)).thenReturn(null);
        assertEquals("errors/error-404", subjectController.getAllTutorsBySubject(1, model));
    }

    /**
     * Tests getAllConsultationBySubject method.
     */
    @Test
    void testGetAllConsultationsBySubject() {
        when(subjectService.getSubjectById(1)).thenReturn(createSubject());
        when(consultationService.getAllConsultations()).thenReturn(new ArrayList<>());
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("consultations", subjectController.getAllConsultationsBySubject(1, model));
    }

    /**
     * Tests getAllConsultationBySubject method with null subject.
     */
    @Test
    void testGetAllConsultationsBySubjectWithNullSubject() {
        when(subjectService.getSubjectById(1)).thenReturn(null);
        assertEquals("errors/error-404", subjectController.getAllConsultationsBySubject(1, model));
    }

    /**
     * Tests mySubjects method.
     */
    @Test
    void testMySubjects() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        when(subjectService.getAllSubjects()).thenReturn(new ArrayList<>());
        assertEquals("select-subjects", subjectController.mySubjects(model));
    }

    /**
     * Tests mySubjects method with invalid user (without tutor role).
     */
    @Test
    void testMySubjectsWithError403() {
        User user = createUser();
        user.setRoles(new HashSet<>());
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        assertEquals("errors/error-403", subjectController.mySubjects(model));
    }

    /**
     * Tests updateMySubjects method.
     */
    @Test
    void testUpdateMySubjects() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(userService.updateUser(user)).thenReturn(user);
        when(subjectService.getSubjectById(1)).thenReturn(createSubject());
        when(subjectService.getAllSubjects()).thenReturn(new ArrayList<>());
        assertEquals("select-subjects", subjectController.updateMySubjects(new ArrayList<>(Collections.singletonList(1)), model));
    }

    /**
     * Tests updateMySubjects method with null subject IDs.
     */
    @Test
    void testUpdateMySubjectsWithNullSubjects() {
        User user = createUser();
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        when(userService.updateUser(user)).thenReturn(user);
        when(subjectService.getAllSubjects()).thenReturn(new ArrayList<>());
        assertEquals("select-subjects", subjectController.updateMySubjects(null, model));
    }

    /**
     * Tests updateMySubjects method with invalid user (without tutor role).
     */
    @Test
    void testUpdateMySubjectsWithError403() {
        User user = createUser();
        user.setRoles(new HashSet<>());
        when(userAuthentication.getCurrentUser()).thenReturn(user);
        assertEquals("errors/error-403", subjectController.updateMySubjects(new ArrayList<>(), model));
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
        user.setSubjects(new HashSet<>(Collections.singletonList(createSubject())));
        return user;
    }

    /**
     * Helper method for subject creation.
     * @return new subject
     */
    public Subject createSubject() {
        Subject subject = new Subject();
        subject.setId(1);
        subject.setSubjectName("Math");
        return subject;
    }
}
