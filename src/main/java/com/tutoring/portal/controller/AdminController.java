package com.tutoring.portal.controller;

import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
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

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserAuthentication userAuthentication;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private static final String USERS = "users";

    @GetMapping(value = "admin/users")
    public String getAllUsers(Model model) {
        logger.info("Searching for all users in the database");

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/{id}")
    public User getUser(@PathVariable int id) {
        String message = "Searching for user wth ID: " + id;
        logger.info(message);
        return userService.getUserById(id);
    }

    @GetMapping(value = "admin/users/register")
    public String registerUser(User user) {
        return "register-user";
    }

    @PostMapping(value = "admin/users/register")
    public String saveUser(@Valid User user, BindingResult result, Model model) {
        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            result.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (result.hasErrors()) {
            logger.error("Cannot save user, wrong input");
            return "register-user";
        }
        userService.saveUser(user);
        logger.info("User successfully saved");

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/update/{id}")
    public String updateUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/update-user";
    }

    @PostMapping(value = "admin/users/update/{id}")
    public String saveUpdatedUser(@PathVariable int id, @Valid User user, BindingResult result, Model model) {
        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null && existingUser.getId() != id) {
            result.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
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

        if (updatedUser.getId() == currentUser.getId()) {
            userAuthentication.updateAuthentication(updatedUser);
        }

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/update-password/{id}")
    public String updateUserPassword(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "errors/error-404";
        }
        model.addAttribute("id", id);
        return "admin/update-password";
    }

    @PostMapping(value = "admin/users/update-password/{id}")
    public String saveUpdatedPassword(@PathVariable int id, @RequestParam String newPassword, Model model) {
        User updatedUser = userService.getUserById(id);
        if (updatedUser == null) {
            return "errors/error-404";
        }
        if (bCryptPasswordEncoder.matches(newPassword, updatedUser.getPassword())) {
            model.addAttribute("newPasswordError", "Provided password matches user's current password");
            logger.error("Cannot update password, wrong input");
            return "admin/update-password";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("newPasswordError", "Password must have at least 6 characters");
            logger.error("Cannot update password, wrong input");
            return "admin/update-password";
        }
        User currentUser = userAuthentication.getCurrentUser();
        updatedUser.setPassword(newPassword);
        userService.updatePassword(updatedUser);
        logger.info("User's password successfully updated");

        if (updatedUser.getId() == currentUser.getId()) {
            userAuthentication.updateAuthentication(updatedUser);
        }

        model.addAttribute("successMessage", "Successfully updated password");
        return "admin/update-password";
    }

    @GetMapping(value = "admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id, Model model) {
        userService.deleteUser(id);
        String message = "Successfully deleted user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/role/add/admin/{id}")
    public String addAdminRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.addAdminRole(user);
        String message = "Successfully added admin role to user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/role/remove/admin/{id}")
    public String removeAdminRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.removeAdminRole(user);
        String message = "Successfully removed admin role from user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/role/add/tutor/{id}")
    public String addTutorRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.addTutorRole(user);
        String message = "Successfully added tutor role to user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/role/remove/tutor/{id}")
    public String removeTutorRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.removeTutorRole(user);
        String message = "Successfully removed admin role from user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/block/{id}")
    public String blockUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.blockUser(user);
        String message = "Successfully blocked user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/unblock/{id}")
    public String unblockUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.unblockUser(user);
        String message = "Successfully unblocked user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/subjects")
    public String getAllSubjects(Model model) {
        logger.info("Searching for all subjects in the database");
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "admin/subjects";
    }

    @GetMapping(value = "admin/subjects/add")
    public String addSubject(Subject subject) {
        return "add-subject";
    }

    @PostMapping(value = "admin/subjects/add")
    public String saveSubject(@Valid Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot save subject, wrong input");
            return "add-subject";
        }
        subjectService.saveSubject(subject);
        logger.info("Subject successfully saved");
        return "redirect:/admin/subjects";
    }

    @GetMapping(value = "admin/subjects/update/{id}")
    public String updateSubject(@PathVariable int id, Model model) {
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        return "update-subject";
    }

    @PostMapping(value = "admin/subjects/update/{id}")
    public String saveUpdatedSubject(@PathVariable int id, @Valid Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot update subject, wrong input");
            return "update-subject";
        }
        Subject updatedSubject = subjectService.getSubjectById(id);
        updatedSubject.setSubjectName(subject.getSubjectName());
        subjectService.saveSubject(updatedSubject);
        logger.info("Subject successfully updated");
        return "redirect:/admin/subjects";
    }

    @GetMapping(value = "admin/subjects/delete/{id}")
    public String deleteSubject(@PathVariable int id, Model model) {
        subjectService.deleteSubject(id);
        String message = "Successfully deleted subjects with ID: " + id;
        logger.info(message);
        return "redirect:/admin/subjects";
    }
}
