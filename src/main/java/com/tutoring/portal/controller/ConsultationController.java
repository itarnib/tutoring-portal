package com.tutoring.portal.controller;

import com.tutoring.portal.model.Consultation;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.ConsultationService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Controller
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(ConsultationController.class);

    @GetMapping(value = "consultations")
    public String getAllConsultations(Model model) {
        logger.info("Searching for all consultations in the database");
        model.addAttribute("consultations", consultationService.getAllConsultations().stream()
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        model.addAttribute("user", userAuthentication.getCurrentUser());
        return "consultations";
    }

    @GetMapping(value = "consultations/my-consultations")
    public String getUserConsultations(Model model) {
        User user = userAuthentication.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("createdConsultationsPast", user.getCreatedConsultations().stream()
                .filter(c -> c.getDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toList()));
        model.addAttribute("createdConsultationsFuture", user.getCreatedConsultations().stream()
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        model.addAttribute("registeredToConsultationsPast", user.getRegisteredToConsultations().stream()
                .filter(c -> c.getDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toList()));
        model.addAttribute("registeredToConsultationsFuture", user.getRegisteredToConsultations().stream()
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        return "my-consultations";
    }

    @GetMapping(value = "consultations/{id}")
    public String getConsultation(@PathVariable int id, Model model) {
        String message = "Searching for consultation wth ID: " + id;
        logger.info(message);
        Consultation consultation = consultationService.getConsultationById(id);
        if (consultation == null) {
            return "errors/error-404";
        }
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultation", consultation);
        return "consultation";
    }

    @GetMapping(value = "consultations/add")
    public String addConsultation(Consultation consultation, Model model) {
        User user = userAuthentication.getCurrentUser();
        consultation.setTutor(user);

        model.addAttribute("consultation", consultation);
        model.addAttribute("subjects", user.getSubjects());
        model.addAttribute("addresses", user.getAddresses());
        return "add-consultation";
    }

    @PostMapping(value = "consultations/add")
    public String saveConsultation(@Valid Consultation consultation, BindingResult result, Model model) {
        if (consultation.getSubject() == null) {
            result.rejectValue("subject", "error.consultation",
                            "Please select a subject from the list");
        }
        if (consultation.getAddress() == null) {
            result.rejectValue("address", "error.consultation",
                            "Please select an address from the list");
        }
        if (consultation.getDateTime() == null || !consultation.getDateTime().isAfter(LocalDateTime.now())) {
            result.rejectValue("dateTime", "error.consultation",
                            "Please provide future date and time");
        }
        if (result.hasErrors()) {
            logger.error("Cannot save consultation, wrong input");
            User user = userAuthentication.getCurrentUser();
            model.addAttribute("addresses", user.getAddresses());
            model.addAttribute("subjects", user.getSubjects());
            return "add-consultation";
        }
        consultationService.saveConsultation(consultation);
        logger.info("Consultation successfully saved");

        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultations", consultationService.getAllConsultations().stream()
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        return "consultations";
    }

    @GetMapping(value = "consultations/update/{id}")
    public String updateConsultation(@PathVariable int id, Model model) {
        Consultation consultation = consultationService.getConsultationById(id);
        User user = userAuthentication.getCurrentUser();

        if (consultation == null) {
            return "errors/error-404";
        }
        if (consultation.getTutor().getId() != user.getId() && !user.isAdmin()) {
            return "errors/error-403";
        }
        if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("warningMessage", "You cannot update past consultations");
            model.addAttribute("user", user);
            model.addAttribute("consultation", consultation);
            return "consultation";
        }

        model.addAttribute("consultation", consultation);
        model.addAttribute("subjects", user.getSubjects());
        model.addAttribute("addresses", user.getAddresses());
        return "update-consultation";
    }

    @PostMapping(value = "consultations/update/{id}")
    public String saveUpdatedConsultation(@PathVariable int id, @Valid Consultation consultation, BindingResult result, Model model) {
        User user = userAuthentication.getCurrentUser();
        Consultation oldConsultation = consultationService.getConsultationById(id);

        if (oldConsultation == null) {
            return "errors/error-404";
        }
        if (consultation.getTutor().getId() != user.getId() && !user.isAdmin()) {
            return "errors/error-403";
        }
        if (consultation.getId() != id) {
            return "errors/error";
        }

        if (consultation.getSubject() == null) {
            result.rejectValue("subject", "error.consultation",
                    "Please select a subject from the list");
        }
        if (consultation.getAddress() == null) {
            result.rejectValue("address", "error.consultation",
                    "Please select an address from the list");
        }
        if (consultation.getDateTime() == null || !consultation.getDateTime().isAfter(LocalDateTime.now())) {
            result.rejectValue("dateTime", "error.consultation",
                    "Please provide future date and time");
        }
        if (result.hasErrors()) {
            logger.error("Cannot update consultation, wrong input");
            model.addAttribute("addresses", user.getAddresses());
            model.addAttribute("subjects", user.getSubjects());
            return "update-consultation";
        }

        consultation.setStudents(oldConsultation.getStudents());
        consultationService.saveConsultation(consultation);
        String message = "Consultation successfully updated";
        logger.info(message);
        model.addAttribute("successMessage", message);
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultation", consultation);
        return "consultation";
    }

    @GetMapping(value = "consultations/delete/{id}")
    public String deleteConsultation(@PathVariable int id, Model model) {
        Consultation consultation = consultationService.getConsultationById(id);
        User currentUser = userAuthentication.getCurrentUser();
        if (consultation == null) {
            return "errors/error-404";
        }
        if (consultation.getTutor().getId() != currentUser.getId() && !currentUser.isAdmin()) {
            return "errors/error-403";
        }
        consultationService.deleteConsultation(id);
        String message = "Successfully deleted consultation with ID: " + id;
        logger.info(message);

        model.addAttribute("successMessage", message);
        model.addAttribute("user", currentUser);
        model.addAttribute("consultations", consultationService.getAllConsultations().stream()
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        return "consultations";
    }

    @GetMapping(value = "consultations/register/{id}")
    public String registerUserToConsultation(@PathVariable int id, Model model) {
        User user = userAuthentication.getCurrentUser();
        Consultation consultation = consultationService.getConsultationById(id);

        if (consultation == null) {
            model.addAttribute("warningMessage", "Consultation with ID " + id + " does not exist");
        } else if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("warningMessage", "You cannot register to past consultations");
        } else if (consultation.getTutor().getId() == user.getId()) {
            model.addAttribute("warningMessage", "You cannot register to your own consultation");
        } else if (consultation.getStudents().contains(user)) {
            model.addAttribute("warningMessage", "You are already registered to this consultation");
        } else if (consultation.getStudents().size() >= consultation.getMaxStudentsNumber()) {
            model.addAttribute("warningMessage", "You cannot register to this consultation, maximum number of students will be exceeded");
        } else {
            consultation.getStudents().add(user);
            consultationService.saveConsultation(consultation);
            String message = "Successfully registered user with ID" + user.getId() + "to consultation with ID: " + id;
            logger.info(message);
            model.addAttribute("successMessage", "You have successfully registered to consultation");
        }

        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultation", consultation);
        return "consultation";
    }

    @GetMapping(value = "consultations/unregister/{id}")
    public String unregisterUserFromConsultation(@PathVariable int id, Model model) {
        User user = userAuthentication.getCurrentUser();
        Consultation consultation = consultationService.getConsultationById(id);

        if (consultation == null) {
            model.addAttribute("warningMessage", "Consultation with ID " + id + " does not exist");
        } else if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("warningMessage", "You cannot unregister from past consultations");
        } else if (!consultation.getStudents().contains(user)) {
            model.addAttribute("warningMessage", "You are not registered to this consultation");
        } else {
            consultation.getStudents().remove(user);
            consultationService.saveConsultation(consultation);
            String message = "Successfully unregistered user with ID " + user.getId() + " from consultation with ID: " + id;
            logger.info(message);
            model.addAttribute("successMessage", "You have successfully unregistered from consultation");
        }

        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute("consultation", consultation);
        return "consultation";
    }
}
