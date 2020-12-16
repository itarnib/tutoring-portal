package com.tutoring.portal.service;

import com.tutoring.portal.model.Subject;
import com.tutoring.portal.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

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
        subjectRepository.deleteById(id);
        return id;
    }
}
