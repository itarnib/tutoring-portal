package com.tutoring.portal.service;

import com.tutoring.portal.model.Address;
import com.tutoring.portal.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Returns address with provided ID or null, if address wasn't found.
     * @param id address ID
     * @return address with provided ID
     */
    public Address getAddressById(int id) {
        Optional<Address> address = addressRepository.findById(id);
        return address.orElse(null);
    }

    /**
     * Saves provided comment and returns it.
     * @param address address
     * @return saved address
     */
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Deletes address with provided ID.
     * @param id address ID
     */
    public void deleteAddress(int id) {
        addressRepository.deleteById(id);
    }
}
