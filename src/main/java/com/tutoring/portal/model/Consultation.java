package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;

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
import javax.validation.constraints.NotEmpty;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name="SUBJECT_ID", nullable=false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
    private User teacher;

    @ManyToMany
    @JoinTable(name = "USER_CONSULTATION",
            joinColumns = @JoinColumn(name = "CONSULTATION_ID", referencedColumnName = "CONSULTATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"))
    private Set<User> students;

    public Consultation() {
    }

    public Consultation(int id, String description, Subject subject, User teacher, Set<User> students) {
        this.id = id;
        this.description = description;
        this.subject = subject;
        this.teacher = teacher;
        this.students = students;
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

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
    }
}
