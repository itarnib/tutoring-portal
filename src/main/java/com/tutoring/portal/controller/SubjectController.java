package com.tutoring.portal.controller;

import com.tutoring.portal.model.Subject;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.ConsultationService;
import com.tutoring.portal.service.SubjectService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.tutoring.portal.util.CommonConstants.SUBJECTS;
import static com.tutoring.portal.util.CommonConstants.SUBJECTS_VIEW;

@Controller
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);

    /**
     * Returns view with all subjects from the database.
     *
     * @param model a Model object used in the view
     * @return subjects view
     */
    @GetMapping(value = "subjects")
    public String getAllSubjects(Model model) {
        logger.info("Searching for all subjects in the database");
        model.addAttribute(SUBJECTS, subjectService.getAllSubjects());
        return SUBJECTS_VIEW;
    }

    /**
     * Returns view with all tutors who teach subject with provided ID.
     * If subject with provided ID doesn't exist, returns error-404 view.
     *
     * @param id subject's ID
     * @param model a Model object used in the view
     * @return tutors view or error-404 view, if subject doesn't exist
     */
    @GetMapping(value = "subjects/{id}/tutors")
    public String getAllTutorsBySubject(@PathVariable int id,  Model model) {
        Subject subject = subjectService.getSubjectById(id);
        // return error-404 view if subject with provided ID doesn't exist
        if (subject == null) {
            return "errors/error-404";
        }

        String message = "Searching for all tutors by subject: " + subject.getSubjectName();
        logger.info(message);
        model.addAttribute("tutors", userService.getAllUsersWithTutorRole().stream()
                // filter list items to contain only tutors who teach provided subject
                .filter(t -> t.getSubjects().contains(subject))
                .collect(Collectors.toList()));

        return "tutors";
    }

    /**
     * Returns view with future consultations where subject ID matches provided ID.
     * If subject with provided ID doesn't exist, returns error-404 view.
     *
     * @param id subject's ID
     * @param model a Model object used in the view
     * @return consultations view or error-404 view, if subject doesn't exist
     */
    @GetMapping(value = "subjects/{id}/consultations")
    public String getAllConsultationsBySubject(@PathVariable int id, Model model) {
        Subject subject = subjectService.getSubjectById(id);
        // return error-404 view if subject with provided ID doesn't exist
        if (subject == null) {
            return "errors/error-404";
        }

        String message = "Searching for all future consultations by subject: " + subject.getSubjectName();
        logger.info(message);

        model.addAttribute("consultations", consultationService.getAllConsultations().stream()
                .filter(c -> c.getSubject().getId() == id)
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("title", "Future Consultations for " + subject.getSubjectName());

        return "consultations";
    }

    /**
     * Returns select-subjects view with selected user's subjects.
     *
     * @param model a Model object used in the view
     * @return select-subjects view or error-403 view, if user is not a tutor
     */
    @GetMapping(value = "my-subjects")
    public String mySubjects(Model model) {
        User user = userAuthentication.getCurrentUser();
        // return error-403 view if user is not a tutor
        if (!user.isTutor()) {
            return "errors/error-403";
        }

        model.addAttribute(SUBJECTS, subjectService.getAllSubjects());
        model.addAttribute("mySubjects", user.getSubjects());
        return "select-subjects";
    }

    /**
     * Updates user's subjects list to contain only subjects with provided IDs.
     *
     * @param subjectIds list withs subject IDs, optional
     * @param model a Model object used in the view
     * @return select-subjects view or error-403 view, if user is not a tutor
     */
    @PostMapping(value = "my-subjects")
    public String updateMySubjects(@RequestParam(required = false) List<Integer> subjectIds, Model model) {
        User user = userAuthentication.getCurrentUser();
        // return error-403 view if user is not a tutor
        if (!user.isTutor()) {
            return "errors/error-403";
        }

        // remove all subjects from user's subjects list
        user.getSubjects().clear();

        // add all selected subjects to user's subjects list
        if (subjectIds != null) {
            for (Integer id : subjectIds) {
                Subject subject = subjectService.getSubjectById(id);
                if (subject != null) {
                    user.getSubjects().add(subject);
                }
            }
        }

        userService.updateUser(user);
        String message = "Successfully updated subjects list for user with ID: " + user.getId();
        logger.info(message);

        model.addAttribute("successMessage", "You have successfully updated your subjects");
        model.addAttribute(SUBJECTS, subjectService.getAllSubjects());
        model.addAttribute("mySubjects", user.getSubjects());

        return "select-subjects";
    }
}
