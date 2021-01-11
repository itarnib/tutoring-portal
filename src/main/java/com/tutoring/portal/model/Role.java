package com.tutoring.portal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * Class for ROLE table.
 */
@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ROLE_ID")
    private int id;

    @Column(name = "ROLE")
    @NotEmpty
    private String role;

    /**
     * Empty constructor.
     */
    public Role() {
    }

    /**
     * Constructor with id and role.
     * @param id id
     * @param role role
     */
    public Role(int id, String role) {
        this.id = id;
        this.role = role;
    }

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
     * Getter for role.
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * Setter for role.
     * @param role new role
     */
    public void setRole(String role) {
        this.role = role;
    }
}
