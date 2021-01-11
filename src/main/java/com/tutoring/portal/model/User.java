package com.tutoring.portal.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Class for USER table.
 */
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "USER_ID")
    private int id;

    @Column(name="NAME")
    @NotEmpty(message = "Please provide a name")
    private String name;

    @Column(name="SURNAME")
    @NotEmpty(message = "Please provide a surname")
    private String surname;

    @Column(name = "EMAIL")
    @Email(message = "Please provide a valid email")
    @NotEmpty(message = "Please provide an email")
    private String email;

    @Column(name = "PASSWORD")
    @Length(min = 6, message = "Password must have at least 6 characters")
    @NotEmpty(message = "Please provide a password")
    private String password;

    @Column(name = "ACTIVE")
    private int active;

    @ManyToMany
    @JoinTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID"))
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(name = "USER_SUBJECT",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "SUBJECT_ID"))
    private Set<Subject> subjects;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tutor")
    private Set<Consultation> createdConsultations;

    @ManyToMany(mappedBy = "students")
    private Set<Consultation> registeredToConsultations;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Address> addresses;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tutor")
    private Set<Comment> receivedComments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "student")
    private Set<Comment> createdComments;

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
     * Getter for name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for surname.
     * @return surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter for surname.
     * @param surname new surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Getter for email.
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email.
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for password.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for password.
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for active.
     * @return active
     */
    public int getActive() {
        return active;
    }

    /**
     * Setter for active.
     * @param active new active
     */
    public void setActive(int active) {
        this.active = active;
    }

    /**
     * Getter for roles.
     * @return roles
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Setter for roles.
     * @param roles new roles
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Getter for subjects.
     * @return subjects
     */
    public Set<Subject> getSubjects() {
        return subjects;
    }

    /**
     * Setter for subjects.
     * @param subjects new subjects
     */
    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    /**
     * Getter for createdConsultations.
     * @return createdConsultations
     */
    public Set<Consultation> getCreatedConsultations() {
        return createdConsultations;
    }

    /**
     * Setter for createdConsultations.
     * @param createdConsultations new createdConsultations
     */
    public void setCreatedConsultations(Set<Consultation> createdConsultations) {
        this.createdConsultations = createdConsultations;
    }

    /**
     * Getter for registeredToConsultations.
     * @return registeredToConsultations
     */
    public Set<Consultation> getRegisteredToConsultations() {
        return registeredToConsultations;
    }

    /**
     * Setter for registeredToConsultations.
     * @param registeredToConsultations new registeredToConsultations
     */
    public void setRegisteredToConsultations(Set<Consultation> registeredToConsultations) {
        this.registeredToConsultations = registeredToConsultations;
    }

    /**
     * Getter for addresses.
     * @return addresses
     */
    public Set<Address> getAddresses() {
        return addresses;
    }

    /**
     * Setter for address.
     * @param addresses new address
     */
    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Getter for receivedComments.
     * @return receivedComments
     */
    public Set<Comment> getReceivedComments() {
        return receivedComments;
    }

    /**
     * Setter for receivedComments.
     * @param receivedComments new receivedComments
     */
    public void setReceivedComments(Set<Comment> receivedComments) {
        this.receivedComments = receivedComments;
    }

    /**
     * Getter for createdComments.
     * @return createdComments
     */
    public Set<Comment> getCreatedComments() {
        return createdComments;
    }

    /**
     * Setter for createdComments.
     * @param createdComments new createdComments
     */
    public void setCreatedComments(Set<Comment> createdComments) {
        this.createdComments = createdComments;
    }

    /**
     * Checks if user has admin role.
     * @return true or false
     */
    public boolean isAdmin() {
        for (Role role : roles) {
            if (role.getRole().equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if user has tutor role.
     * @return true or false
     */
    public boolean isTutor() {
        for (Role role : roles) {
            if (role.getRole().equals("TUTOR")) {
                return true;
            }
        }
        return false;
    }
}
