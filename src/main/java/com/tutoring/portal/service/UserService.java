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
import java.util.Collections;
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

    /**
     * Returns user with provided email.
     * @param email user's email
     * @return user with provided email
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Returns list with all users from the database.
     * @return users list
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    /**
     * Returns list with all users with tutor role.
     * @return tutors list
     */
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

    /**
     * Returns user with provided ID or null, if user wasn't found.
     * @param id user's ID
     * @return user with provided ID
     */
    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    /**
     * Saves provided user and returns it.
     * @param user user
     * @return saved user
     */
    public User saveUser(User user) {
        // encode password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // set as active
        user.setActive(1);
        // grant student role
        Role userRole = roleRepository.findByRole("STUDENT");
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        return userRepository.save(user);
    }

    /**
     * Updates provided user and returns it.
     * @param user updated user
     * @return saved user
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates provided user's password, encodes it and returns updated user.
     * @param user updated user
     * @return saved user
     */
    public User updatePassword(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Deletes user with provided ID.
     * @param id user's ID
     * @return deleted user's ID
     */
    public int deleteUser(int id) {
        User user = getUserById(id);
        List<Consultation> consultations = consultationRepository.findAll();
        // delete user's relationships
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

    /**
     * Grants admin role to user and returns updated user.
     * @param user user
     * @return updated user
     */
    public User addAdminRole(User user) {
        Role adminRole = roleRepository.findByRole("ADMIN");
        Set<Role> userRoles = user.getRoles();
        userRoles.add(adminRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    /**
     * Removes admin role from user's role list and returns updated user.
     * @param user user
     * @return updated user
     */
    public User removeAdminRole(User user) {
        Role adminRole = roleRepository.findByRole("ADMIN");
        Set<Role> userRoles = user.getRoles();
        userRoles.remove(adminRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    /**
     * Grants tutor role to user and returns updated user.
     * @param user user
     * @return updated user
     */
    public User addTutorRole(User user) {
        Role tutorRole = roleRepository.findByRole("TUTOR");
        Set<Role> userRoles = user.getRoles();
        userRoles.add(tutorRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    /**
     * Removes tutor role from user's role list and returns updated user.
     * @param user user
     * @return updated user
     */
    public User removeTutorRole(User user) {
        Role tutorRole = roleRepository.findByRole("TUTOR");
        Set<Role> userRoles = user.getRoles();
        userRoles.remove(tutorRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    /**
     * Blocks provided user and returns updated user.
     * @param user user
     * @return updated user
     */
    public User blockUser(User user) {
        user.setActive(0);
        return userRepository.save(user);
    }

    /**
     * Unblocks provided user and returns updated user.
     * @param user user
     * @return updated user
     */
    public User unblockUser(User user) {
        user.setActive(1);
        return userRepository.save(user);
    }
}
