package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Class for CONSULTATION table.
 */
@Entity
@Table(name = "CONSULTATION")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CONSULTATION_ID")
    private int id;

    @Column(name="DESCRIPTION")
    @NotEmpty(message = "Please provide a description")
    @Length(min = 20, message = "Description must have at least 20 characters")
    private String description;

    @Column(name="MAX_STUDENTS_NUMBER")
    @Min(value = 1, message = "Maximum number of students must be equal or greater than 1")
    private int maxStudentsNumber;

    @Column(name="DATE_TIME")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name="SUBJECT_ID", nullable=false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
    private User tutor;

    @ManyToMany
    @JoinTable(name = "USER_CONSULTATION",
            joinColumns = @JoinColumn(name = "CONSULTATION_ID", referencedColumnName = "CONSULTATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"))
    private Set<User> students;

    @ManyToOne
    @JoinColumn(name="ADDRESS_ID", nullable=false)
    private Address address;

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
     * Getter for description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description.
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for maxStudentsNumber.
     * @return maxStudentsNumber
     */
    public int getMaxStudentsNumber() {
        return maxStudentsNumber;
    }

    /**
     * Setter for maxStudentsNumber
     * @param maxStudentsNumber new maxStudentsNumber
     */
    public void setMaxStudentsNumber(int maxStudentsNumber) {
        this.maxStudentsNumber = maxStudentsNumber;
    }

    /**
     * Getter for dateTime.
     * @return dateTime
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Setter for dateTime.
     * @param dateTime new dateTime
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Getter for subject.
     * @return subject
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Setter for subject.
     * @param subject new subject
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Getter for tutor.
     * @return tutor
     */
    public User getTutor() {
        return tutor;
    }

    /**
     * Setter for tutor.
     * @param tutor new tutor
     */
    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    /**
     * Getter for students.
     * @return students
     */
    public Set<User> getStudents() {
        return students;
    }

    /**
     * Setter for students.
     * @param students new students
     */
    public void setStudents(Set<User> students) {
        this.students = students;
    }

    /**
     * Getter for address.
     * @return address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Setter for address.
     * @param address new address
     */
    public void setAddress(Address address) {
        this.address = address;
    }
}
