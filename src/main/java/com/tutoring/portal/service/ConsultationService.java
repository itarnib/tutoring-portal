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

    public List<Consultation> getAllConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        consultationRepository.findAll().forEach(consultations::add);
        return consultations;
    }

    public Consultation getConsultationById(int id) {
        Optional<Consultation> consultation = consultationRepository.findById(id);
        if(consultation.isPresent()) {
            return consultation.get();
        }
        return null;
    }

    public Consultation saveConsultation(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    public int deleteConsultation(int id) {
        consultationRepository.deleteById(id);
        return id;
    }
}
