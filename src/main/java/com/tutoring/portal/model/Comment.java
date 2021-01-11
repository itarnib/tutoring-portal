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

/**
 * Class for COMMENT table.
 */
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
     * Getter for timestamp.
     * @return timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Setter for timestamp.
     * @param timestamp new timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter for feedback.
     * @return feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Setter for feedback.
     * @param feedback new feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
     * Getter for student.
     * @return student
     */
    public User getStudent() {
        return student;
    }

    /**
     * Setter for student.
     * @param student new student
     */
    public void setStudent(User student) {
        this.student = student;
    }
}
