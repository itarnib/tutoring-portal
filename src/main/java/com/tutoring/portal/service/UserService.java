package com.tutoring.portal.service;

import com.tutoring.portal.model.User;
import com.tutoring.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Long deleteUser(Long id) {
        userRepository.deleteById(id);
        return id;
    }
}
