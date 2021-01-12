package com.tutoring.portal.repository;

import com.tutoring.portal.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for Consultation.
 */
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

}