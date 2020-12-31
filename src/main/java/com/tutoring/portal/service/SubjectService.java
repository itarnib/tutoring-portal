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

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        subjectRepository.findAll().forEach(subjects::add);
        return subjects;
    }

    public Subject getSubjectById(int id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if(subject.isPresent()) {
            return subject.get();
        }
        return null;
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public int deleteSubject(int id) {
        Subject subject = getSubjectById(id);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getSubjects().contains(subject)) {
                user.getSubjects().remove(subject);
                userRepository.save(user);
            }
        }
        subjectRepository.deleteById(id);
        return id;
    }
}
