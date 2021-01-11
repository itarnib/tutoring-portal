package com.tutoring.portal.service;

import com.tutoring.portal.model.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class SubjectServiceTests {

    @Autowired
    SubjectService subjectService;

    /**
     * Tests if subject is saved.
     */
    @Test
    void testSaveSubject() {
        Subject subject = createSubject();
        Subject savedSubject = subjectService.saveSubject(subject);

        assertEquals(subject.getId(), savedSubject.getId());
        assertEquals(subject.getSubjectName(), savedSubject.getSubjectName());
    }

    /**
     * Tests getSubjectById method.
     */
    @Test
    void testGetSubjectById() {
        Subject savedSubject = subjectService.saveSubject(createSubject());
        Subject foundSubject = subjectService.getSubjectById(savedSubject.getId());

        assertThat(foundSubject)
            .returns(savedSubject.getId(), from(Subject::getId))
            .returns(savedSubject.getSubjectName(), from(Subject::getSubjectName));
    }

    /**
     * Tests getAllSubjects method.
     */
    @Test
    void testGetAllSubjects() {
        subjectService.saveSubject(createSubject());
        Subject subject2 = new Subject();
        subject2.setId(2);
        subject2.setSubjectName("English");
        subjectService.saveSubject(subject2);

        List<Subject> subjects = subjectService.getAllSubjects();
        assertEquals(2, subjects.size());
        assertTrue(subjects.stream().anyMatch(subject -> "Math".equals(subject.getSubjectName())));
        assertTrue(subjects.stream().anyMatch(subject -> "English".equals(subject.getSubjectName())));
    }

    /**
     * Tests subject's deletion.
     */
    @Test
    void testDeleteSubject() {
        Subject subject = subjectService.saveSubject(createSubject());
        subjectService.deleteSubject(subject.getId());

        assertNull(subjectService.getSubjectById(subject.getId()));
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
