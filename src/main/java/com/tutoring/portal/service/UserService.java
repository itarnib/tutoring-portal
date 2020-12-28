package com.tutoring.portal.service;

import com.tutoring.portal.model.Consultation;
import com.tutoring.portal.model.Role;
import com.tutoring.portal.model.User;
import com.tutoring.portal.repository.ConsultationRepository;
import com.tutoring.portal.repository.RoleRepository;
import com.tutoring.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public List<User> getAllUsersWithTutorRole() {
        List<User> users = new ArrayList<>(userRepository.findAll());
        List<User> tutors = new ArrayList<>();
        for (User user : users) {
            if (user.isTutor()) {
                tutors.add(user);
            }
        }
        return tutors;
    }

    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public int deleteUser(int id) {
        User user = getUserById(id);
        List<Consultation> consultations = consultationRepository.findAll();
        for (Consultation consultation : consultations) {
            if (consultation.getStudents().contains(user)) {
                consultation.getStudents().remove(user);
                consultationRepository.save(consultation);
            }
        }
        user.setRoles(null);
        userRepository.save(user);
        userRepository.deleteById(id);
        return id;
    }

    public User addAdminRole(User user) {
        Role adminRole = roleRepository.findByRole("ADMIN");
        Set<Role> userRoles = user.getRoles();
        userRoles.add(adminRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    public User removeAdminRole(User user) {
        Role adminRole = roleRepository.findByRole("ADMIN");
        Set<Role> userRoles = user.getRoles();
        userRoles.remove(adminRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    public User addTutorRole(User user) {
        Role tutorRole = roleRepository.findByRole("TUTOR");
        Set<Role> userRoles = user.getRoles();
        userRoles.add(tutorRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    public User removeTutorRole(User user) {
        Role tutorRole = roleRepository.findByRole("TUTOR");
        Set<Role> userRoles = user.getRoles();
        userRoles.remove(tutorRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    public User blockUser(User user) {
        user.setActive(0);
        return userRepository.save(user);
    }

    public User unblockUser(User user) {
        user.setActive(1);
        return userRepository.save(user);
    }
}
