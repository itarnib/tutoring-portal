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

import static com.tutoring.portal.util.CommonConstants.COMMENTS_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_403_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_404_VIEW;

@Controller
public class TutorController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(TutorController.class);

    /**
     * Returns view with all tutors.
     *
     * @param model a Model object used in the view
     * @return tutors view
     */
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
        String message = "Searching for tutor wth ID: " + id;
        logger.info(message);
        User tutor = userService.getUserById(id);

        // return error-404 view if user with provided ID doesn't exist or is not a tutor
        if (tutor == null || !tutor.isTutor()) {
            return ERROR_404_VIEW;
        }

        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultations", tutor.getCreatedConsultations().stream()
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("title", "Future Consultations with " + tutor.getName() + " " + tutor.getSurname());

        return "consultations";
    }

    /**
     * Returns view with tutor's comments and form for new comments.
     * If tutor with provided ID doesn't exist, returns error-404 view.
     *
     * @param id tutor's ID
     * @param comment new comment
     * @param model a Model object used in the view
     * @return comments view or error-404 view, if tutor with provided ID doesn't exist
     */
    @GetMapping(value = "tutors/{id}/comments")
    public String getTutorComments(@PathVariable int id, Comment comment, Model model) {
        String message = "Searching for tutor wth ID: " + id;
        logger.info(message);
        User tutor = userService.getUserById(id);
        // check if tutor with provided ID exists
        if (tutor == null || !tutor.isTutor()) {
            return ERROR_404_VIEW;
        }
        model.addAttribute("tutor", tutor);
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("comments", tutor.getReceivedComments());
        return COMMENTS_VIEW;
    }

    /**
     * Validates provided comment, saves it and returns comments view with success or error messages.
     * If tutor with provided ID doesn't exist, returns error-404 view.
     * If tutor ID equals current user's ID, returns error-403 view.
     *
     * @param comment new comment
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return comments view or error view, if error occurred
     */
    @PostMapping(value = "tutors/{id}/comments/add")
    public String addComment(@PathVariable int id, @Valid Comment comment, BindingResult result, Model model) {
        User tutor = userService.getUserById(id);
        User currentUser = userAuthentication.getCurrentUser();
        // check if tutor with provided ID exists
        if (tutor == null || !tutor.isTutor()) {
            return ERROR_404_VIEW;
        }
        // check if user is trying to add comment about himself
        if (tutor.getId() == currentUser.getId()) {
            return ERROR_403_VIEW;
        }
        // check if comment is invalid
        if (result.hasErrors()) {
            logger.error("Cannot save comment, wrong input");
            model.addAttribute("tutor", tutor);
            model.addAttribute("user", currentUser);
            model.addAttribute("comments", tutor.getReceivedComments());
            return COMMENTS_VIEW;
        }
        comment.setStudent(currentUser);
        comment.setTutor(tutor);
        commentService.saveComment(comment);
        logger.info("Comment successfully saved");
        return "redirect:/tutors/" + id + "/comments";
    }
}
