package com.tutoring.portal.controller;

import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.ConsultationService;
import com.tutoring.portal.service.SubjectService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);

    @GetMapping(value = "subjects")
    public String getAllSubjects(Model model) {
        logger.info("Searching for all subjects in the database");
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @GetMapping(value = "subjects/{id}/tutors")
    public String getAllTutorsBySubject(@PathVariable int id,  Model model) {
        Subject subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return "errors/error-404";
        }
        logger.info("Searching for all tutors by subject: " + subject.getSubjectName());
        model.addAttribute("tutors", userService.getAllUsersWithTutorRole().stream()
                .filter(t -> t.getSubjects().contains(subject)).collect(Collectors.toList()));
        return "tutors";
    }

    @GetMapping(value = "subjects/{id}/consultations")
    public String getAllConsultationsBySubject(@PathVariable int id, Model model) {
        Subject subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return "errors/error-404";
        }
        logger.info("Searching for all future consultations by subject: " + subject.getSubjectName());
        model.addAttribute("consultations", consultationService.getAllConsultations().stream()
                .filter(c -> c.getSubject().getId() == id)
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("user", userAuthentication.getCurrentUser());
        return "consultations";
    }

    @GetMapping(value = "my-subjects")
    public String mySubjects(Model model) {
        User user = userAuthentication.getCurrentUser();
        if (!user.isTutor()) {
            return "errors/error-403";
        }
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("mySubjects", user.getSubjects());
        return "select-subjects";
    }

    @PostMapping(value = "my-subjects")
    public String updateMySubjects(@RequestParam List<Integer> subjectIds, Model model) {
        User user = userAuthentication.getCurrentUser();
        if (!user.isTutor()) {
            return "errors/error-403";
        }
        user.getSubjects().clear();
        for (Integer id : subjectIds) {
            Subject subject = subjectService.getSubjectById(id);
            if (subject != null) {
                user.getSubjects().add(subject);
            }
        }
        userService.updateUser(user);
        logger.info("Successfully updated subjects list for user with ID: " + user.getId());
        model.addAttribute("successMessage", "You have successfully updated your subjects");
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("mySubjects", user.getSubjects());
        return "select-subjects";
    }
}
