package com.tutoring.portal.service;

import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
import com.tutoring.portal.repository.SubjectRepository;
import com.tutoring.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns list with all subjects from the database.
     * @return subjects list
     */
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjectRepository.findAll());
    }

    /**
     * Returns subject with provided ID or null, if subject wasn't found.
     * @param id subject's ID
     * @return subject with provided ID
     */
    public Subject getSubjectById(int id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        return subject.orElse(null);
    }

    /**
     * Saves provided subject and returns it.
     * @param subject subject
     * @return saved subject
     */
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    /**
     * Deletes subject with provided ID.
     * @param id subject's ID
     */
    public void deleteSubject(int id) {
        Subject subject = getSubjectById(id);
        List<User> users = userRepository.findAll();
        // delete subject's relationships
        for (User user : users) {
            if (user.getSubjects().contains(subject)) {
                user.getSubjects().remove(subject);
                userRepository.save(user);
            }
        }
        subjectRepository.deleteById(id);
    }
}
