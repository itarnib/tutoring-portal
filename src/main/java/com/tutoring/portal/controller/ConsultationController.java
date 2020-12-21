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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("consultations", user.getConsultations());
        return "consultations";
    }

    @GetMapping(value = "consultations/{id}")
    public Consultation getConsultation(@PathVariable int id) {
        String message = "Searching for consultation wth ID: " + id;
        logger.info(message);
        return consultationService.getConsultationById(id);
    }

    @GetMapping(value = "consultations/add")
    public String addConsultation(Consultation consultation, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        consultation.setTeacher(user);

        model.addAttribute("consultation", consultation);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "add-consultation";
    }

    @PostMapping(value = "consultations/add")
    public String saveConsultation(Consultation consultation, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot save consultation, wrong input");
            return "add-consultation";
        }
        Consultation newConsultation = new Consultation();
        newConsultation.setId(consultation.getId());
        newConsultation.setTeacher(consultation.getTeacher());
        newConsultation.setSubject(consultation.getSubject());
        newConsultation.setDescription(consultation.getDescription());
        consultationService.saveConsultation(newConsultation);
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
}
