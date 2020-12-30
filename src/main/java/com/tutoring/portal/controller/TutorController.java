package com.tutoring.portal.controller;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.CommentService;
import com.tutoring.portal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Controller
public class TutorController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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
    public String getTutorComments(@PathVariable int id, Comment comment, Model model) {
        logger.info("Searching for tutor wth ID: " + id);
        User tutor = userService.getUserById(id);
        if (tutor == null || !tutor.isTutor()) {
            return "errors/error-404";
        }
        model.addAttribute("tutor", tutor);
        model.addAttribute("user", getCurrentUser());
        model.addAttribute("comments", tutor.getReceivedComments());
        return "comments";
    }

    @PostMapping(value = "tutors/{id}/comments/add")
    public String addComment(@PathVariable int id, @Valid Comment comment, BindingResult result, Model model) {
        User tutor = userService.getUserById(id);
        if (tutor == null || !tutor.isTutor()) {
            return "errors/error-404";
        }
        if (tutor.getId() == getCurrentUser().getId()) {
            return "errors/error-403";
        }
        if (result.hasErrors()) {
            logger.error("Cannot save comment, wrong input");
            model.addAttribute("tutor", tutor);
            model.addAttribute("comments", tutor.getReceivedComments());
            return "comments";
        }
        comment.setStudent(getCurrentUser());
        comment.setTutor(tutor);
        commentService.saveComment(comment);
        logger.info("Comment successfully saved");
        return "redirect:/tutors/" + id + "/comments";
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(auth.getName());
    }
}
