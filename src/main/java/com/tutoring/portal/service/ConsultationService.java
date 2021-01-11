package com.tutoring.portal.service;

import com.tutoring.portal.model.Consultation;
import com.tutoring.portal.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    /**
     * Returns list with all consultations from the database.
     * @return consultations list
     */
    public List<Consultation> getAllConsultations() {
        return new ArrayList<>(consultationRepository.findAll());
    }

    /**
     * Returns consultation with provided ID or null, if consultation wasn't found.
     * @param id consultation's ID
     * @return consultation with provided ID
     */
    public Consultation getConsultationById(int id) {
        Optional<Consultation> consultation = consultationRepository.findById(id);
        return consultation.orElse(null);
    }

    /**
     * Saves provided consultation and returns it.
     * @param consultation consultation
     * @return saved consultation
     */
    public Consultation saveConsultation(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    /**
     * Deletes consultation with provided ID.
     * @param id consultation's ID
     */
    public void deleteConsultation(int id) {
        consultationRepository.deleteById(id);
    }
}
