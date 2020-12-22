package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

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

    @OneToMany(mappedBy="subject")
    private Set<Consultation> consultations;

    public Subject() {
    }

    public Subject(int id, String subjectName, Set<Consultation> consultations) {
        this.id = id;
        this.subjectName = subjectName;
        this.consultations = consultations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Set<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }
}
