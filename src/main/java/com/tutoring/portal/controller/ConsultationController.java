package com.tutoring.portal.controller;

import com.tutoring.portal.model.Consultation;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.ConsultationService;
import com.tutoring.portal.service.SubjectService;
import com.tutoring.portal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    private static final Logger logger = LoggerFactory.getLogger(ConsultationController.class);

    @GetMapping(value = "consultations")
    public String getAllConsultations(Model model) {
        logger.info("Searching for all consultations in the database");
        model.addAttribute("consultations", consultationService.getAllConsultations());
        return "consultations";
    }

    @GetMapping(value = "consultations/my-consultations")
    public String getUserConsultations(Model model) {
        User user = getCurrentUser();
        model.addAttribute("createdConsultations", user.getCreatedConsultations());
        model.addAttribute("registeredToConsultations", user.getRegisteredToConsultations());
        return "my-consultations";
    }

    @GetMapping(value = "consultations/{id}")
    public Consultation getConsultation(@PathVariable int id) {
        String message = "Searching for consultation wth ID: " + id;
        logger.info(message);
        return consultationService.getConsultationById(id);
    }

    @GetMapping(value = "consultations/add")
    public String addConsultation(Consultation consultation, Model model) {
        User user = getCurrentUser();
        consultation.setTeacher(user);

        model.addAttribute("consultation", consultation);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "add-consultation";
    }

    @PostMapping(value = "consultations/add")
    public String saveConsultation(@Valid Consultation consultation, BindingResult result, Model model) {
        if (consultation.getSubject() == null) {
            result
                    .rejectValue("subject", "error.consultation",
                            "Please select a subject from the list");
        }
        if (result.hasErrors()) {
            logger.error("Cannot save consultation, wrong input");
            model.addAttribute("subjects", subjectService.getAllSubjects());
            return "add-consultation";
        }
        consultationService.saveConsultation(consultation);
        logger.info("Consultation successfully saved");

        model.addAttribute("consultations", consultationService.getAllConsultations());
        return "consultations";
    }

    @GetMapping(value = "consultations/delete/{id}")
    public String deleteConsultation(@PathVariable int id, Model model) {
        consultationService.deleteConsultation(id);
        String message = "Successfully deleted consultation with ID: " + id;
        logger.info(message);

        model.addAttribute("consultations", consultationService.getAllConsultations());
        return "consultations";
    }

    @GetMapping(value = "consultations/register/{id}")
    public String registerUserToConsultation(@PathVariable int id, Model model) {
        User user = getCurrentUser();
        Consultation consultation = consultationService.getConsultationById(id);

        if (consultation == null) {
            model.addAttribute("warningMessage", "Consultation with ID " + id + " does not exist");
        } else if (consultation.getTeacher().getId() == user.getId()) {
            model.addAttribute("warningMessage", "You cannot register to your own consultation");
        } else if (consultation.getStudents().contains(user)) {
            model.addAttribute("warningMessage", "You are already registered to this consultation");
        } else {
            consultation.getStudents().add(user);
            consultationService.saveConsultation(consultation);
            String message = "Successfully registered user with ID" + user.getId() + "to consultation with ID: " + id;
            logger.info(message);
            model.addAttribute("successMessage", "You have successfully registered to consultation");
        }

        model.addAttribute("consultations", consultationService.getAllConsultations());
        return "consultations";
    }

    @GetMapping(value = "consultations/unregister/{id}")
    public String unregisterUserFromConsultation(@PathVariable int id, Model model) {
        User user = getCurrentUser();
        Consultation consultation = consultationService.getConsultationById(id);

        if (consultation == null) {
            model.addAttribute("warningMessage", "Consultation with ID " + id + " does not exist");
        } else if (!consultation.getStudents().contains(user)) {
            model.addAttribute("warningMessage", "You are not registered to this consultation");
        } else {
            consultation.getStudents().remove(user);
            consultationService.saveConsultation(consultation);
            String message = "Successfully unregistered user with ID " + user.getId() + " from consultation with ID: " + id;
            logger.info(message);
            model.addAttribute("successMessage", "You have successfully unregistered from consultation");
        }

        model.addAttribute("createdConsultations", user.getCreatedConsultations());
        model.addAttribute("registeredToConsultations", user.getRegisteredToConsultations());
        return "my-consultations";
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(auth.getName());
    }
}
