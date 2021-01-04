package com.tutoring.portal.controller;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.CommentService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(TutorController.class);

    @GetMapping(value = "tutors")
    public String getAllTutors(Model model) {
        logger.info("Searching for all users with tutor role in the database");
        model.addAttribute("tutors", userService.getAllUsersWithTutorRole());
        return "tutors";
    }

    /**
     * Returns view with future consultations where tutor ID matches provided ID.
     * If tutor with provided ID doesn't exist, returns error-404 view.
     *
     * @param id tutor's ID
     * @param model a Model object used in the view
     * @return consultations view or error-404 view, if tutor with provided ID doesn't exist
     */
    @GetMapping(value = "tutors/{id}/consultations")
    public String getTutorConsultations(@PathVariable int id,  Model model) {
        logger.info("Searching for tutor wth ID: " + id);
        User tutor = userService.getUserById(id);

        // return error-404 view if user with provided ID doesn't exist or is not a tutor
        if (tutor == null || !tutor.isTutor()) {
            return "errors/error-404";
        }

        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultations", tutor.getCreatedConsultations().stream()
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("title", "Future Consultations with " + tutor.getName() + " " + tutor.getSurname());

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
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("comments", tutor.getReceivedComments());
        return "comments";
    }

    @PostMapping(value = "tutors/{id}/comments/add")
    public String addComment(@PathVariable int id, @Valid Comment comment, BindingResult result, Model model) {
        User tutor = userService.getUserById(id);
        if (tutor == null || !tutor.isTutor()) {
            return "errors/error-404";
        }
        if (tutor.getId() == userAuthentication.getCurrentUser().getId()) {
            return "errors/error-403";
        }
        if (result.hasErrors()) {
            logger.error("Cannot save comment, wrong input");
            model.addAttribute("tutor", tutor);
            model.addAttribute("comments", tutor.getReceivedComments());
            return "comments";
        }
        comment.setStudent(userAuthentication.getCurrentUser());
        comment.setTutor(tutor);
        commentService.saveComment(comment);
        logger.info("Comment successfully saved");
        return "redirect:/tutors/" + id + "/comments";
    }
}
