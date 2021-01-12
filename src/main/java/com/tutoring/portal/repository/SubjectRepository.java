package com.tutoring.portal.repository;

import com.tutoring.portal.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for Subject.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

}