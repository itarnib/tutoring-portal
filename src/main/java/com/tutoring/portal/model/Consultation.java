package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "CONSULTATION")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CONSULTATION_ID")
    private int id;

    @Column(name="DESCRIPTION")
    @NotEmpty(message = "Please provide a description")
    @Length(min = 6, message = "Password must have at least 6 characters")
    private String description;

    @ManyToOne
    @JoinColumn(name="SUBJECT_ID", nullable=false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
    private User user;

    public Consultation() {
    }

    public Consultation(int id, String description, Subject subject, User user) {
        this.id = id;
        this.description = description;
        this.subject = subject;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
