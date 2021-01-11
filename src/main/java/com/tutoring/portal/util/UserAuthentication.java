package com.tutoring.portal.util;

import com.tutoring.portal.model.User;
import com.tutoring.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Common methods for user authentication.
 */
@Component
public class UserAuthentication {

    @Autowired
    UserService userService;

    /**
     * Returns user, who is currently authenticated.
     *
     * @return current user
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(auth.getName());
    }

    /**
     * Updates user authentication when user's data is updated.
     *
     * @param user current user
     */
    public void updateAuthentication(User user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(),
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
