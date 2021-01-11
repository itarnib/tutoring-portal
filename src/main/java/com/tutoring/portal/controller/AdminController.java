package com.tutoring.portal.controller;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.CommentService;
import com.tutoring.portal.service.SubjectService;
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

import static com.tutoring.portal.util.CommonConstants.ADMIN_SUBJECTS_VIEW;
import static com.tutoring.portal.util.CommonConstants.ADMIN_UPDATE_PASSWORD_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_404_VIEW;
import static com.tutoring.portal.util.CommonConstants.REDIRECT;
import static com.tutoring.portal.util.CommonConstants.SUCCESS_MESSAGE;
import static com.tutoring.portal.util.CommonConstants.USERS;
import static com.tutoring.portal.util.CommonConstants.USERS_VIEW;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserAuthentication userAuthentication;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Returns view with all users.
     *
     * @param model a Model object used in the view
     * @return users view
     */
    @GetMapping(value = "admin/users")
    public String getAllUsers(Model model) {
        logger.info("Searching for all users in the database");
        model.addAttribute("users", userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Returns view with the form for new user creation.
     *
     * @param user new user
     * @return register-user view
     */
    @GetMapping(value = "admin/users/register")
    public String registerUser(User user) {
        return "admin/register-user";
    }

    /**
     * Validates provided user, saves it and returns view with all users.
     * If provided user is invalid, returns view with new user creation form.
     *
     * @param user new user
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return users view or register-user view, if provided user is invalid
     */
    @PostMapping(value = "admin/users/register")
    public String saveUser(@Valid User user, BindingResult result, Model model) {
        // check if user with the same email already exists
        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            result.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        // check if provided user is invalid
        if (result.hasErrors()) {
            logger.error("Cannot save user, wrong input");
            return "admin/register-user";
        }

        userService.saveUser(user);
        logger.info("User successfully saved");
        model.addAttribute("users", userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Returns view with user update form if provided ID is valid.
     * Otherwise returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return update-user view or error-404 view, if provided ID is invalid
     */
    @GetMapping(value = "admin/users/update/{id}")
    public String updateUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        model.addAttribute("user", user);
        return "admin/update-user";
    }

    /**
     * Validates provided user, saves it and returns view with all users.
     * If provided user is invalid, returns view with user update form.
     *
     * @param id user's ID
     * @param user updated user
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return users view or update-user view, if provided user is invalid
     */
    @PostMapping(value = "admin/users/update/{id}")
    public String saveUpdatedUser(@PathVariable int id, @Valid User user, BindingResult result, Model model) {
        // check if another user with the same email already exists
        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null && existingUser.getId() != id) {
            result.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        // check if user is invalid
        if (result.hasErrors()) {
            logger.error("Cannot update user, wrong input");
            return "admin/update-user";
        }

        User currentUser = userAuthentication.getCurrentUser();
        User updatedUser = userService.getUserById(id);
        updatedUser.setName(user.getName());
        updatedUser.setSurname(user.getSurname());
        updatedUser.setEmail(user.getEmail());
        userService.updateUser(updatedUser);
        logger.info("User successfully updated");

        // if updated user is current user, update authentication
        if (updatedUser.getId() == currentUser.getId()) {
            userAuthentication.updateAuthentication(updatedUser);
        }

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Returns view with password update form, if provided ID is valid.
     * Otherwise returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return update password view or error-404 view, if provided ID is invalid
     */
    @GetMapping(value = "admin/users/update-password/{id}")
    public String updateUserPassword(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        model.addAttribute("id", id);
        return ADMIN_UPDATE_PASSWORD_VIEW;
    }

    /**
     * Updates user's password and returns update password view with success message.
     * If provided user's ID is invalid, returns error-404 view.
     * If new password is invalid, returns update password view with error messages.
     *
     * @param id user's ID
     * @param newPassword new password
     * @param model a Model object used in the view
     * @return update password view or error-404 view, if provided user's ID is invalid
     */
    @PostMapping(value = "admin/users/update-password/{id}")
    public String saveUpdatedPassword(@PathVariable int id, @RequestParam String newPassword, Model model) {
        User updatedUser = userService.getUserById(id);
        // check if user exists
        if (updatedUser == null) {
            return ERROR_404_VIEW;
        }
        // check if new password matches user's current password
        if (bCryptPasswordEncoder.matches(newPassword, updatedUser.getPassword())) {
            model.addAttribute("newPasswordError", "Provided password matches user's current password");
            logger.error("Cannot update password, wrong input");
            return ADMIN_UPDATE_PASSWORD_VIEW;
        }
        // check if new password contains less than 6 characters
        if (newPassword.length() < 6) {
            model.addAttribute("newPasswordError", "Password must have at least 6 characters");
            logger.error("Cannot update password, wrong input");
            return ADMIN_UPDATE_PASSWORD_VIEW;
        }
        User currentUser = userAuthentication.getCurrentUser();
        updatedUser.setPassword(newPassword);
        userService.updatePassword(updatedUser);
        logger.info("User's password successfully updated");

        // if updated user is current user, update authentication
        if (updatedUser.getId() == currentUser.getId()) {
            userAuthentication.updateAuthentication(updatedUser);
        }

        model.addAttribute(SUCCESS_MESSAGE, "Successfully updated password");
        return ADMIN_UPDATE_PASSWORD_VIEW;
    }

    /**
     * Deletes user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.deleteUser(id);
        String message = "Successfully deleted user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Adds admin role to user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/role/add/admin/{id}")
    public String addAdminRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.addAdminRole(user);
        String message = "Successfully added admin role to user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Removes admin role from user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/role/remove/admin/{id}")
    public String removeAdminRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.removeAdminRole(user);
        String message = "Successfully removed admin role from user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Adds tutor role to user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/role/add/tutor/{id}")
    public String addTutorRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.addTutorRole(user);
        String message = "Successfully added tutor role to user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Removes tutor role from user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/role/remove/tutor/{id}")
    public String removeTutorRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.removeTutorRole(user);
        String message = "Successfully removed admin role from user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Blocks user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/block/{id}")
    public String blockUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.blockUser(user);
        String message = "Successfully blocked user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Unblocks user with provided ID and returns view with all users.
     * If user with provided ID doesn't exist, returns error-404 view.
     *
     * @param id user's ID
     * @param model a Model object used in the view
     * @return users view or error-404 view, if provided user's ID is invalid
     */
    @GetMapping(value = "admin/users/unblock/{id}")
    public String unblockUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        // check if user exists
        if (user == null) {
            return ERROR_404_VIEW;
        }
        userService.unblockUser(user);
        String message = "Successfully unblocked user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS_VIEW;
    }

    /**
     * Returns admin view with all subjects from the database.
     *
     * @param model a Model object used in the view
     * @return admin subjects view
     */
    @GetMapping(value = "admin/subjects")
    public String getAllSubjects(Model model) {
        logger.info("Searching for all subjects in the database");
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return ADMIN_SUBJECTS_VIEW;
    }

    /**
     * Returns view with the form for new subject creation.
     *
     * @param subject new subject
     * @return add-subject view
     */
    @GetMapping(value = "admin/subjects/add")
    public String addSubject(Subject subject) {
        return "add-subject";
    }

    /**
     * Validates provided subject, saves it and returns redirect to admin view with all subjects.
     * If provided subject is invalid, returns view with new subject creation form.
     *
     * @param subject new subject
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return admin subject view or add-subject view, if provided subject is invalid
     */
    @PostMapping(value = "admin/subjects/add")
    public String saveSubject(@Valid Subject subject, BindingResult result, Model model) {
        // check if subject is invalid
        if (result.hasErrors()) {
            logger.error("Cannot save subject, wrong input");
            return "add-subject";
        }
        subjectService.saveSubject(subject);
        logger.info("Subject successfully saved");
        return REDIRECT + ADMIN_SUBJECTS_VIEW;
    }

    /**
     * Returns view with subject update form if provided ID is valid.
     * Otherwise returns error-404 view.
     *
     * @param id subject's ID
     * @param model a Model object used in the view
     * @return update-subject view or error-404 view, if provided ID is invalid
     */
    @GetMapping(value = "admin/subjects/update/{id}")
    public String updateSubject(@PathVariable int id, Model model) {
        Subject subject = subjectService.getSubjectById(id);
        // check if subject exists
        if (subject == null) {
            return ERROR_404_VIEW;
        }
        model.addAttribute("subject", subject);
        return "update-subject";
    }

    /**
     * Validates provided subject, saves it and returns redirect to admin view with all subjects.
     * If provided subject is invalid, returns view with subject update form.
     *
     * @param id subject's ID
     * @param subject updated subject
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return admin subjects view or update-subject view, if provided subject is invalid
     */
    @PostMapping(value = "admin/subjects/update/{id}")
    public String saveUpdatedSubject(@PathVariable int id, @Valid Subject subject, BindingResult result, Model model) {
        // check if subject is invalid
        if (result.hasErrors()) {
            logger.error("Cannot update subject, wrong input");
            return "update-subject";
        }
        Subject updatedSubject = subjectService.getSubjectById(id);
        updatedSubject.setSubjectName(subject.getSubjectName());
        subjectService.saveSubject(updatedSubject);
        logger.info("Subject successfully updated");
        return REDIRECT + ADMIN_SUBJECTS_VIEW;
    }

    /**
     * Deletes subject with provided ID and redirects user to subjects view.
     * If subject with provided ID doesn't exist, returns error-404 view.
     *
     * @param id comment's ID
     * @return redirect to subjects view or error-404 view if subject doesn't exist
     */
    @GetMapping(value = "admin/subjects/delete/{id}")
    public String deleteSubject(@PathVariable int id) {
        Subject subject = subjectService.getSubjectById(id);
        // return error-404 view if subject doesn't exist
        if (subject == null) {
            return ERROR_404_VIEW;
        }

        subjectService.deleteSubject(id);
        String message = "Successfully deleted subject with ID: " + id;
        logger.info(message);
        return REDIRECT + ADMIN_SUBJECTS_VIEW;
    }

    /**
     * Deletes comment with provided ID and redirects user to tutor's comments view.
     * If comment with provided ID doesn't exist, returns error-404 view.
     *
     * @param id comment's ID
     * @return redirect to tutor's comments view or error-404 view if comment doesn't exist
     */
    @GetMapping(value = "admin/comments/delete/{id}")
    public String deleteComment(@PathVariable int id) {
        Comment comment = commentService.getCommentById(id);
        // return error-404 view if comment doesn't exist
        if (comment == null) {
            return ERROR_404_VIEW;
        }

        int tutorId = comment.getTutor().getId();
        commentService.deleteComment(id);
        String message = "Successfully deleted comment with ID: " + id;
        logger.info(message);
        return REDIRECT + "tutors/" + tutorId + "/comments";
    }
}
