package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMENT_ID")
    private int id;

    @Column(name="TIMESTAMP")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timestamp;

    @Column(name="FEEDBACK")
    @NotEmpty(message = "Please provide a feedback")
    @Length(min = 10, message = "Feedback must have at least 10 characters")
    private String feedback;

    @ManyToOne
    @JoinColumn(name="TUTOR_ID", nullable=false)
    private User tutor;

    @ManyToOne
    @JoinColumn(name="STUDENT_ID", nullable=false)
    private User student;

    public Comment() {
    }

    public Comment(int id, LocalDateTime timestamp, String feedback, User tutor, User student) {
        this.id = id;
        this.timestamp = timestamp;
        this.feedback = feedback;
        this.tutor = tutor;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }
}
