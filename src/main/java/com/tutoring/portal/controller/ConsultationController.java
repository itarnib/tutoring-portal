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

import static com.tutoring.portal.util.CommonConstants.ADDRESSES;
import static com.tutoring.portal.util.CommonConstants.CONSULTATION;
import static com.tutoring.portal.util.CommonConstants.CONSULTATIONS;
import static com.tutoring.portal.util.CommonConstants.CONSULTATIONS_VIEW;
import static com.tutoring.portal.util.CommonConstants.CONSULTATION_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_403_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_404_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_CONSULTATION;
import static com.tutoring.portal.util.CommonConstants.SUBJECTS;
import static com.tutoring.portal.util.CommonConstants.SUCCESS_MESSAGE;
import static com.tutoring.portal.util.CommonConstants.TITLE;
import static com.tutoring.portal.util.CommonConstants.WARNING_MESSAGE;

@Controller
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(ConsultationController.class);

    /**
     * Returns a view with past consultations.
     *
     * @param model a Model object used in the view
     * @return consultations view
     */
    @GetMapping(value = "consultations/past")
    public String getAllPastConsultations(Model model) {
        logger.info("Searching for all past consultations");

        model.addAttribute(CONSULTATIONS, consultationService.getAllConsultations().stream()
                // filter list items to contain only past consultations
                .filter(c -> c.getDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute(TITLE, "Past Consultations");

        return CONSULTATIONS_VIEW;
    }

    /**
     * Returns a view with future consultations.
     *
     * @param model a Model object used in the view
     * @return consultations view
     */
    @GetMapping(value = "consultations/future")
    public String getAllFutureConsultations(Model model) {
        logger.info("Searching for all future consultations");

        model.addAttribute(CONSULTATIONS, consultationService.getAllConsultations().stream()
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute(TITLE, "Future Consultations");

        return CONSULTATIONS_VIEW;
    }

    /**
     * Returns a view with all user's consultations.
     *
     * @param model a Model object used in the view
     * @return my-consultations view
     */
    @GetMapping(value = "consultations/my-consultations")
    public String getUserConsultations(Model model) {
        User user = userAuthentication.getCurrentUser();
        model.addAttribute("user", user);

        // created consultations
        model.addAttribute("createdConsultationsPast", user.getCreatedConsultations().stream()
                // filter list items to contain only past consultations
                .filter(c -> c.getDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("createdConsultationsFuture", user.getCreatedConsultations().stream()
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));

        // registered to consultations
        model.addAttribute("registeredToConsultationsPast", user.getRegisteredToConsultations().stream()
                // filter list items to contain only past consultations
                .filter(c -> c.getDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList()));
        model.addAttribute("registeredToConsultationsFuture", user.getRegisteredToConsultations().stream()
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        return "my-consultations";
    }

    /**
     * Returns view with consultation's data or error-404 view, if consultation with provided ID doesn't exist.
     *
     * @param id consultation's ID
     * @param model model a Model object used in the view
     * @return consultation view or error-404 view, if provided ID is invalid
     */
    @GetMapping(value = "consultations/{id}")
    public String getConsultation(@PathVariable int id, Model model) {
        String message = "Searching for consultation wth ID: " + id;
        logger.info(message);
        Consultation consultation = consultationService.getConsultationById(id);
        // check if consultation exists
        if (consultation == null) {
            return ERROR_404_VIEW;
        }
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute(CONSULTATION, consultation);
        return CONSULTATION_VIEW;
    }

    /**
     * Returns view with the form for new consultation creation.
     *
     * @param consultation new consultation
     * @param model model a Model object used in the view
     * @return add-consultation view
     */
    @GetMapping(value = "consultations/add")
    public String addConsultation(Consultation consultation, Model model) {
        User user = userAuthentication.getCurrentUser();
        consultation.setTutor(user);
        model.addAttribute(CONSULTATION, consultation);
        model.addAttribute(SUBJECTS, user.getSubjects());
        model.addAttribute(ADDRESSES, user.getAddresses());
        return "add-consultation";
    }

    /**
     * Saves consultation and redirects to future consultations view, if provided consultation object is valid.
     * If validation error occurs, returns add-consultation view.
     *
     * @param consultation created Consultation object
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return redirect to future consultations view or add-consultation view if validation error occurs
     */
    @PostMapping(value = "consultations/add")
    public String saveConsultation(@Valid Consultation consultation, BindingResult result, Model model) {
        if (consultation.getSubject() == null) {
            result.rejectValue("subject", ERROR_CONSULTATION,
                            "Please select a subject from the list");
        }
        if (consultation.getAddress() == null) {
            result.rejectValue("address", ERROR_CONSULTATION,
                            "Please select an address from the list");
        }
        if (consultation.getDateTime() == null || !consultation.getDateTime().isAfter(LocalDateTime.now())) {
            result.rejectValue("dateTime", ERROR_CONSULTATION,
                            "Please provide future date and time");
        }
        if (result.hasErrors()) {
            logger.error("Cannot save consultation, wrong input");
            User user = userAuthentication.getCurrentUser();
            model.addAttribute(ADDRESSES, user.getAddresses());
            model.addAttribute(SUBJECTS, user.getSubjects());
            return "add-consultation";
        }
        consultationService.saveConsultation(consultation);
        logger.info("Consultation successfully saved");

        return "redirect:/consultations/future";
    }

    /**
     * Returns view with consultation update form.
     * If provided ID is invalid, returns error-404 view.
     * If user is not consultation's creator and not admin, returns error-403 view.
     * If it is past consultation, returns consultation view with error message.
     *
     * @param id consultation's ID
     * @param model a Model object used in the view
     * @return update-consultation view or error view, if error occurs
     */
    @GetMapping(value = "consultations/update/{id}")
    public String updateConsultation(@PathVariable int id, Model model) {
        Consultation consultation = consultationService.getConsultationById(id);
        User user = userAuthentication.getCurrentUser();
        // check if consultation exists
        if (consultation == null) {
            return ERROR_404_VIEW;
        }
        // check if user has rights to update consultation
        if (consultation.getTutor().getId() != user.getId() && !user.isAdmin()) {
            return ERROR_403_VIEW;
        }
        // check if it is past consultation
        if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute(WARNING_MESSAGE, "You cannot update past consultations");
            model.addAttribute("user", user);
            model.addAttribute(CONSULTATION, consultation);
            return CONSULTATION_VIEW;
        }

        User tutor = userService.getUserById(consultation.getTutor().getId());
        model.addAttribute(CONSULTATION, consultation);
        model.addAttribute(SUBJECTS, tutor.getSubjects());
        model.addAttribute(ADDRESSES, tutor.getAddresses());
        return "update-consultation";
    }

    /**
     * Validates provided consultation, saves it and returns view with consultation's data.
     * If provided ID is invalid, returns error-404 view.
     * If user is not consultation's creator and not admin, returns error-403 view.
     * If provided consultation is invalid, returns view with consultation update form.
     *
     * @param id consultation's ID
     * @param consultation updated consultation
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return consultation view or update-consultation view, if provided consultation is invalid, or error view, if error occurs
     */
    @PostMapping(value = "consultations/update/{id}")
    public String saveUpdatedConsultation(@PathVariable int id, @Valid Consultation consultation, BindingResult result, Model model) {
        User user = userAuthentication.getCurrentUser();
        Consultation oldConsultation = consultationService.getConsultationById(id);
        // check if consultation exists
        if (oldConsultation == null) {
            return ERROR_404_VIEW;
        }
        // check if user has rights to update consultation
        if (consultation.getTutor().getId() != user.getId() && !user.isAdmin()) {
            return ERROR_403_VIEW;
        }
        // check if provided ID matches updated consultation's ID
        if (consultation.getId() != id) {
            return "errors/error";
        }
        // check if subject is selected
        if (consultation.getSubject() == null) {
            result.rejectValue("subject", ERROR_CONSULTATION,
                    "Please select a subject from the list");
        }
        // check if address is selected
        if (consultation.getAddress() == null) {
            result.rejectValue("address", ERROR_CONSULTATION,
                    "Please select an address from the list");
        }
        // check if provided time is future time
        if (consultation.getDateTime() == null || !consultation.getDateTime().isAfter(LocalDateTime.now())) {
            result.rejectValue("dateTime", ERROR_CONSULTATION,
                    "Please provide future date and time");
        }
        // check if consultation is valid
        if (result.hasErrors()) {
            logger.error("Cannot update consultation, wrong input");
            User tutor = userService.getUserById(consultation.getTutor().getId());
            model.addAttribute(ADDRESSES, tutor.getAddresses());
            model.addAttribute(SUBJECTS, tutor.getSubjects());
            return "update-consultation";
        }

        consultation.setStudents(oldConsultation.getStudents());
        consultationService.saveConsultation(consultation);
        String message = "Consultation successfully updated";
        logger.info(message);

        model.addAttribute(SUCCESS_MESSAGE, message);
        model.addAttribute("user", userAuthentication.getCurrentUser());
        model.addAttribute(CONSULTATION, consultation);
        return CONSULTATION_VIEW;
    }

    /**
     * Deletes consultation with provided ID.
     * Returns view with future consultations if deletion was successful.
     * If consultation with provided ID doesn't exist, returns error-404 view.
     * If current user doesn't have permission to delete this consultation, returns error-403 view.
     *
     * @param id consultation's ID
     * @param model a Model object used in the view
     * @return consultations view or error view, if error occurs
     */
    @GetMapping(value = "consultations/delete/{id}")
    public String deleteConsultation(@PathVariable int id, Model model) {
        Consultation consultation = consultationService.getConsultationById(id);
        User currentUser = userAuthentication.getCurrentUser();

        // return error-404 view if consultation with provided ID doesn't exist
        if (consultation == null) {
            return ERROR_404_VIEW;
        }
        // return error-403 view if current user doesn't have permission to delete this consultation
        if (consultation.getTutor().getId() != currentUser.getId() && !currentUser.isAdmin()) {
            return ERROR_403_VIEW;
        }

        consultationService.deleteConsultation(id);
        String message = "Successfully deleted consultation with ID: " + id;
        logger.info(message);

        model.addAttribute(SUCCESS_MESSAGE, message);
        model.addAttribute("user", currentUser);
        model.addAttribute(TITLE, "Future Consultations");
        model.addAttribute(CONSULTATIONS, consultationService.getAllConsultations().stream()
                // filter list items to contain only future consultations
                .filter(c -> c.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));

        return CONSULTATIONS_VIEW;
    }

    /**
     * Registers user to consultation and returns consultation view.
     * If consultation with provided ID doesn't exist, returns error-404 view.
     *
     * @param id consultation's ID
     * @param model a Model object used in the view
     * @return consultation view or error-404 view if provided ID is invalid
     */
    @GetMapping(value = "consultations/register/{id}")
    public String registerUserToConsultation(@PathVariable int id, Model model) {
        User user = userAuthentication.getCurrentUser();
        Consultation consultation = consultationService.getConsultationById(id);
        // check if consultation exists
        if (consultation == null) {
            return ERROR_404_VIEW;
        }
        // check if it is a past consultation
        if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute(WARNING_MESSAGE, "You cannot register to past consultations");
        // check if current user is consultation's creator
        } else if (consultation.getTutor().getId() == user.getId()) {
            model.addAttribute(WARNING_MESSAGE, "You cannot register to your own consultation");
        // check if user is already registered to this consultation
        } else if (consultation.getStudents().contains(user)) {
            model.addAttribute(WARNING_MESSAGE, "You are already registered to this consultation");
        // check if maximum students number will be exceeded
        } else if (consultation.getStudents().size() >= consultation.getMaxStudentsNumber()) {
            model.addAttribute(WARNING_MESSAGE, "You cannot register to this consultation, maximum number of students will be exceeded");
        } else {
            consultation.getStudents().add(user);
            consultationService.saveConsultation(consultation);
            String message = "Successfully registered user with ID" + user.getId() + "to consultation with ID: " + id;
            logger.info(message);
            model.addAttribute(SUCCESS_MESSAGE, "You have successfully registered to consultation");
        }

        model.addAttribute("user", user);
        model.addAttribute(CONSULTATION, consultation);
        return CONSULTATION_VIEW;
    }

    /**
     * Unregisters user from consultation and returns consultation view.
     * If consultation with provided ID doesn't exist, returns error-404 view.
     *
     * @param id consultation's ID
     * @param model a Model object used in the view
     * @return consultation view or error-404 view if provided ID is invalid
     */
    @GetMapping(value = "consultations/unregister/{id}")
    public String unregisterUserFromConsultation(@PathVariable int id, Model model) {
        User user = userAuthentication.getCurrentUser();
        Consultation consultation = consultationService.getConsultationById(id);
        // check if consultation exists
        if (consultation == null) {
            return ERROR_404_VIEW;
        }
        // check if it is a past consultation
        if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute(WARNING_MESSAGE, "You cannot unregister from past consultations");
        // check if user is registered to this consultation
        } else if (!consultation.getStudents().contains(user)) {
            model.addAttribute(WARNING_MESSAGE, "You are not registered to this consultation");
        } else {
            consultation.getStudents().remove(user);
            consultationService.saveConsultation(consultation);
            String message = "Successfully unregistered user with ID " + user.getId() + " from consultation with ID: " + id;
            logger.info(message);
            model.addAttribute(SUCCESS_MESSAGE, "You have successfully unregistered from consultation");
        }

        model.addAttribute("user", user);
        model.addAttribute(CONSULTATION, consultation);
        return CONSULTATION_VIEW;
    }
}
