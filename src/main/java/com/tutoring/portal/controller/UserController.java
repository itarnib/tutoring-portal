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

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value="/login")
    public String login(){
        return "login";
    }

    @GetMapping(value="/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String createNewUser(@Valid User user, BindingResult bindingResult, Model model) {
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (!bindingResult.hasErrors()) {
            userService.saveUser(user);
            model.addAttribute("successMessage", "User has been registered successfully");
            model.addAttribute("user", new User());
        }
        return "registration";
    }

    @GetMapping(value="/")
    public String home(Model model){
        model.addAttribute("user", getCurrentUser());
        return "index";
    }

    @GetMapping(value="/my-comments")
    public String myComments(Model model) {
        User user = getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("receivedComments", user.getReceivedComments());
        model.addAttribute("createdComments", user.getCreatedComments());
        return "my-comments";
    }

    @GetMapping(value="/my-comments/update/{id}")
    public String myCommentsUpdate(@PathVariable int id, Model model) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return "errors/error-404";
        }
        if (getCurrentUser().getId() != comment.getStudent().getId()) {
            return "errors/error-403";
        }
        model.addAttribute("comment", comment);
        return "update-comment";
    }

    @PostMapping(value="/my-comments/update/{id}")
    public String saveUpdatedComment(@PathVariable int id, @Valid Comment comment, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot update comment, wrong input");
            model.addAttribute("comment", comment);
            return "update-comment";
        }
        Comment updatedComment = commentService.getCommentById(id);
        updatedComment.setFeedback(comment.getFeedback());
        commentService.saveComment(updatedComment);
        logger.info("Comment successfully updated");
        return "redirect:/my-comments";
    }

    @GetMapping(value="/my-comments/delete/{id}")
    public String myCommentsDelete(@PathVariable int id, Model model) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return "errors/error-404";
        }
        if (getCurrentUser().getId() != comment.getStudent().getId()) {
            return "errors/error-403";
        }
        commentService.deleteComment(id);
        logger.info("Successfully deleted comment with ID: " + id);
        return "redirect:/my-comments";
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(auth.getName());
    }
}
