package com.tutoring.portal.controller;

import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.SubjectService;
import com.tutoring.portal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

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
    public String saveUser(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot save user, wrong input");
            return "register-user";
        }
        User newUser = new User(user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword(), user.getActive(), user.getRoles(), user.getSubjects());
        userService.saveUser(newUser);
        logger.info("User successfully saved");

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id, Model model) {
        userService.deleteUser(id);
        String message = "Successfully deleted user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/adminrole/add/{id}")
    public String addAdminRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.addAdminRole(user);
        String message = "Successfully added admin role to user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/users/adminrole/remove/{id}")
    public String removeAdminRole(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        userService.removeAdminRole(user);
        String message = "Successfully removed admin role from user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "admin/subjects")
    public String getAllSubjects(Model model) {
        logger.info("Searching for all subjects in the database");
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @GetMapping(value = "admin/subjects/add")
    public String addSubject(Subject subject) {
        return "add-subject";
    }

    @PostMapping(value = "admin/subjects/add")
    public String saveSubject(Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot save subject, wrong input");
            return "add-subject";
        }
        Subject newSubject = new Subject(subject.getId(), subject.getSubjectName());
        subjectService.saveSubject(newSubject);
        logger.info("Subject successfully saved");

        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @GetMapping(value = "admin/subjects/delete/{id}")
    public String deleteSubject(@PathVariable int id, Model model) {
        subjectService.deleteSubject(id);
        String message = "Successfully deleted subjects with ID: " + id;
        logger.info(message);

        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }
}
