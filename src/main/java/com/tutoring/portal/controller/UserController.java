package com.tutoring.portal.controller;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.CommentService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static com.tutoring.portal.util.CommonConstants.ERROR_403_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_404_VIEW;
import static com.tutoring.portal.util.CommonConstants.SUCCESS_MESSAGE;
import static com.tutoring.portal.util.CommonConstants.UPDATE_PASSWORD_ERROR;
import static com.tutoring.portal.util.CommonConstants.UPDATE_PASSWORD_VIEW;
import static com.tutoring.portal.util.CommonConstants.UPDATE_USER_VIEW;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserAuthentication userAuthentication;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Returns view with login form.
     *
     * @return login view
     */
    @GetMapping(value="/login")
    public String login(){
        return "login";
    }

    /**
     * Returns view with registration form.
     *
     * @param user new user
     * @return registration view
     */
    @GetMapping(value="/registration")
    public String registration(User user) {
        return "registration";
    }

    /**
     * Validates provided user, saves it and returns registration view with success message.
     * If provided user is invalid, returns registration view with error messages.
     *
     * @param user new user
     * @param bindingResult a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return users view or register-user view, if provided user is invalid
     */
    @PostMapping(value = "/registration")
    public String createNewUser(@Valid User user, BindingResult bindingResult, Model model) {
        User userExists = userService.findUserByEmail(user.getEmail());
        // check if user with the same email already exists
        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        // check if provided user is valid
        if (!bindingResult.hasErrors()) {
            userService.saveUser(user);
            model.addAttribute(SUCCESS_MESSAGE, "User has been registered successfully");
            model.addAttribute("user", new User());
        }
        return "registration";
    }

    /**
     * Returns home view.
     *
     * @param model a Model object used in the view
     * @return index view
     */
    @GetMapping(value="/")
    public String home(Model model){
        model.addAttribute("user", userAuthentication.getCurrentUser());
        return "index";
    }

    /**
     * Returns view with all user's comments.
     *
     * @param model a Model object used in the view
     * @return my-comments view
     */
    @GetMapping(value="/my-comments")
    public String myComments(Model model) {
        User user = userAuthentication.getCurrentUser();
        model.addAttribute("user", user);
        // received comments
        model.addAttribute("receivedComments", user.getReceivedComments());
        // created comments
        model.addAttribute("createdComments", user.getCreatedComments());
        return "my-comments";
    }

    /**
     * Returns view with comment update form.
     * If provided ID is invalid, returns error-404 view.
     * If user is not comment's creator, returns error-403 view.
     *
     * @param id comment's ID
     * @param model a Model object used in the view
     * @return update-comment view or error view, if error occurs
     */
    @GetMapping(value="/my-comments/update/{id}")
    public String myCommentsUpdate(@PathVariable int id, Model model) {
        Comment comment = commentService.getCommentById(id);
        // check if comment exists
        if (comment == null) {
            return ERROR_404_VIEW;
        }
        // check if current user is comment's author
        if (userAuthentication.getCurrentUser().getId() != comment.getStudent().getId()) {
            return ERROR_403_VIEW;
        }
        model.addAttribute("comment", comment);
        return "update-comment";
    }

    /**
     * Validates provided comment, saves it and returns redirect to view with user's comments.
     * If provided ID is invalid, returns error-404 view.
     * If user is not comment's creator, returns error-403 view.
     * If provided comment is invalid, returns view with comment update form.
     *
     * @param id comment's ID
     * @param comment updated comment
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return my-comments view or update-comment view, if provided comment is invalid, or error view, if error occurs
     */
    @PostMapping(value="/my-comments/update/{id}")
    public String saveUpdatedComment(@PathVariable int id, @Valid Comment comment, BindingResult result, Model model) {
        Comment updatedComment = commentService.getCommentById(id);
        // check if comment exists
        if (updatedComment == null) {
            return ERROR_404_VIEW;
        }
        // check if current user is comment's author
        if (userAuthentication.getCurrentUser().getId() != updatedComment.getStudent().getId()) {
            return ERROR_403_VIEW;
        }
        // check if comment is invalid
        if (result.hasErrors()) {
            logger.error("Cannot update comment, wrong input");
            model.addAttribute("comment", comment);
            return "update-comment";
        }
        updatedComment.setFeedback(comment.getFeedback());
        commentService.saveComment(updatedComment);
        logger.info("Comment successfully updated");
        return "redirect:/my-comments";
    }

    /**
     * Deletes comment with provided ID and returns view with user's comments if deletion was successful.
     * If comment with provided ID doesn't exist, returns error-404 view.
     * If current user is not comment's creator, returns error-403 view.
     *
     * @param id comment's ID
     * @param model a Model object used in the view
     * @return my-comments view or error view, if error occurs
     */
    @GetMapping(value="/my-comments/delete/{id}")
    public String myCommentsDelete(@PathVariable int id, Model model) {
        Comment comment = commentService.getCommentById(id);
        // check if comment exists
        if (comment == null) {
            return ERROR_404_VIEW;
        }
        // check if current user is comment's author
        if (userAuthentication.getCurrentUser().getId() != comment.getStudent().getId()) {
            return ERROR_403_VIEW;
        }
        commentService.deleteComment(id);
        String message = "Successfully deleted comment with ID: " + id;
        logger.info(message);
        return "redirect:/my-comments";
    }

    /**
     * Returns view with user's profile update form.
     *
     * @param model a Model object used in the view
     * @return update-user view
     */
    @GetMapping(value="/profile/update")
    public String updateProfile(Model model) {
        model.addAttribute("user", userAuthentication.getCurrentUser());
        return UPDATE_USER_VIEW;
    }

    /**
     * Validates provided user, saves it, if it is valid, and returns view with user's profile update form
     * and success or error messages.
     *
     * @param user updated user
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return user's profile update form
     */
    @PostMapping(value = "/profile/update")
    public String saveUpdatedProfile(@Valid User user, BindingResult result, Model model) {
        User currentUser = userAuthentication.getCurrentUser();
        User existingUser = userService.findUserByEmail(user.getEmail());
        // check if another user with the same email already exists
        if (existingUser != null && existingUser.getId() != currentUser.getId()) {
            result.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        // check if user is invalid
        if (result.hasErrors()) {
            logger.error("Cannot update user, wrong input");
            return UPDATE_USER_VIEW;
        }
        currentUser.setName(user.getName());
        currentUser.setSurname(user.getSurname());
        currentUser.setEmail(user.getEmail());
        userService.updateUser(currentUser);
        logger.info("User successfully updated");

        // update authentication
        userAuthentication.updateAuthentication(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("successMessage", "Successfully updated profile data");
        return UPDATE_USER_VIEW;
    }

    /**
     * Returns view with password update form.
     *
     * @return update password view
     */
    @GetMapping(value="/update-password")
    public String updatePassword() {
        return UPDATE_PASSWORD_VIEW;
    }

    /**
     * Validates provided passwords, saves new password and returns same view with success message.
     * If provided passwords are invalid, returns update password view with error messages.
     *
     * @param oldPassword old password
     * @param newPassword new password
     * @param model a Model object used in the view
     * @return update password view with success or error messages
     */
    @PostMapping(value = "/update-password")
    public String saveUpdatedPassword(@RequestParam String oldPassword, @RequestParam String newPassword, Model model) {
        User currentUser = userAuthentication.getCurrentUser();
        // check if provided old password matches user's current password
        if (!bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            model.addAttribute("oldPasswordError", "Provided password does not match your current password");
            logger.error(UPDATE_PASSWORD_ERROR);
            return UPDATE_PASSWORD_VIEW;
        }
        // check if new password matches user's current password
        if (bCryptPasswordEncoder.matches(newPassword, currentUser.getPassword())) {
            model.addAttribute("newPasswordError", "Provided password matches your current password");
            logger.error(UPDATE_PASSWORD_ERROR);
            return UPDATE_PASSWORD_VIEW;
        }
        // check if new password contains less than 6 characters
        if (newPassword.length() < 6) {
            model.addAttribute("newPasswordError", "Password must have at least 6 characters");
            logger.error(UPDATE_PASSWORD_ERROR);
            return UPDATE_PASSWORD_VIEW;
        }
        currentUser.setPassword(newPassword);
        userService.updatePassword(currentUser);
        logger.info("Password successfully updated");
        // update authentication
        userAuthentication.updateAuthentication(currentUser);

        model.addAttribute("successMessage", "Successfully updated password");
        return UPDATE_PASSWORD_VIEW;
    }

    /**
     * Deletes user's profile.
     *
     * @return redirect to logout
     */
    @GetMapping(value="/profile/delete")
    public String deleteProfile() {
        int userId = userAuthentication.getCurrentUser().getId();
        userService.deleteUser(userId);
        String message = "Successfully deleted user with ID: " + userId;
        logger.info(message);
        return "redirect:/logout";
    }
}
