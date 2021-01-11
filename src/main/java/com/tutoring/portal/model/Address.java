package com.tutoring.portal.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Class for ADDRESS table.
 */
@Entity
@Table(name = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ADDRESS_ID")
    private int id;

    @Column(name="COUNTRY")
    @NotEmpty(message = "Please provide a country")
    private String country;

    @Column(name="CITY")
    @NotEmpty(message = "Please provide a city")
    private String city;

    @Column(name="STREET")
    @NotEmpty(message = "Please provide a street")
    private String street;

    @Column(name="STREET_NUMBER")
    @NotEmpty(message = "Please provide a street number")
    private String streetNumber;

    @ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "address")
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
     * Getter for country.
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setter for country.
     * @param country new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Getter for city.
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for city.
     * @param city new city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter for street.
     * @return street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Setter for street.
     * @param street new street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Getter for streetNumber.
     * @return streetNumber
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Setter for streetNumber.
     * @param streetNumber new streetNumber
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Getter for user.
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter for user.
     * @param user new user
     */
    public void setUser(User user) {
        this.user = user;
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
