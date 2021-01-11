package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Class for SUBJECT table.
 */
@Entity
@Table(name = "SUBJECT")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SUBJECT_ID")
    private int id;

    @Column(name = "SUBJECT_NAME")
    @Length(min = 2, message = "Subject name must have at least 2 characters")
    @NotEmpty(message = "Please provide a subject name")
    private String subjectName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="subject")
    private Set<Consultation> consultations;

    /**
     * Getter for id.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for id.
     * @param id new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for subjectName.
     * @return subjectName
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Setter for subjectName
     * @param subjectName new subjectName
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * Getter for consultations.
     * @return consultations
     */
    public Set<Consultation> getConsultations() {
        return consultations;
    }

    /**
     * Setter for consultations.
     * @param consultations new consultations
     */
    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }
}
