package com.tutoring.portal.repository;

import com.tutoring.portal.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for Address.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

}