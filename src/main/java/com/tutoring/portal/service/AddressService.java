package com.tutoring.portal.service;

import com.tutoring.portal.model.Address;
import com.tutoring.portal.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> getAllAddresses() {
        List<Address> addresses = new ArrayList<>();
        addressRepository.findAll().forEach(addresses::add);
        return addresses;
    }

    public Address getAddressById(int id) {
        Optional<Address> address = addressRepository.findById(id);
        if(address.isPresent()) {
            return address.get();
        }
        return null;
    }

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    public int deleteAddress(int id) {
        addressRepository.deleteById(id);
        return id;
    }
}
