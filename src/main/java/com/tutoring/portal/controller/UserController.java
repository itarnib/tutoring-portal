package com.tutoring.portal.controller;

import com.tutoring.portal.model.User;
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
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final String USERS = "users";

    @GetMapping(value = "/users")
    public String getAllUsers(Model model) {
        logger.info("Searching for all users in the database");

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable Long id) {
        String message = "Searching for user wth ID: " + id;
        logger.info(message);
        return userService.getUserById(id);
    }

    @GetMapping(value = "/users/register")
    public String registerUser(User user) {
        return "register-user";
    }

    @PostMapping(value = "/users/register")
    public String saveUser(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot save user, wrong input");
            return "register-user";
        }
        User newUser = new User(user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword());
        userService.saveUser(newUser);
        logger.info("User successfully saved");

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }

    @GetMapping(value = "users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        userService.deleteUser(id);
        String message = "Successfully deleted user with ID: " + id;
        logger.info(message);

        model.addAttribute(USERS, userService.getAllUsers());
        return USERS;
    }
}
