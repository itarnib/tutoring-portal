package com.tutoring.portal.controller;

import com.tutoring.portal.model.User;
import com.tutoring.portal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Controller
public class TutorController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(TutorController.class);

    @GetMapping(value = "tutors")
    public String getAllTutors(Model model) {
        logger.info("Searching for all users with tutor role in the database");
        model.addAttribute("tutors", userService.getAllUsersWithTutorRole());
        return "tutors";
    }

    @GetMapping(value = "tutors/{id}/consultations")
    public String getTutorConsultations(@PathVariable int id,  Model model) {
        logger.info("Searching for tutor wth ID: " + id);
        User tutor = userService.getUserById(id);
        if (tutor == null || !tutor.isTutor()) {
            return "errors/error-404";
        }
        model.addAttribute("user", getCurrentUser());
        model.addAttribute("consultations", tutor.getCreatedConsultations().stream()
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        return "consultations";
    }

    @GetMapping(value = "tutors/{id}/comments")
    public String getTutorComments(@PathVariable int id,  Model model) {
        logger.info("Searching for tutor wth ID: " + id);
        User tutor = userService.getUserById(id);
        if (tutor == null || !tutor.isTutor()) {
            return "errors/error-404";
        }
        model.addAttribute("tutor", tutor);
        model.addAttribute("comments", tutor.getReceivedComments());
        return "comments";
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(auth.getName());
    }
}
