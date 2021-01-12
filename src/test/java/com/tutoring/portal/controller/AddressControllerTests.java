package com.tutoring.portal.controller;

import com.tutoring.portal.model.Address;
import com.tutoring.portal.model.Role;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.AddressService;
import com.tutoring.portal.util.UserAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AddressControllerTests {

    @InjectMocks
    AddressController addressController;

    @Mock
    AddressService addressService;

    @Mock
    UserAuthentication userAuthentication;

    @Mock
    Model model;

    @Mock
    BindingResult result;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests getUserAddresses method.
     */
    @Test
    void testGetUserAddresses() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("addresses", addressController.getUserAddresses(model));
    }

    /**
     * Tests addAddress method.
     */
    @Test
    void testAddAddress() {
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("add-address", addressController.addAddress(model));
    }

    /**
     * Tests saveAddress method.
     */
    @Test
    void testSaveAddress() {
        Address address = createAddress();
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        when(addressService.saveAddress(address)).thenReturn(address);
        assertEquals("addresses", addressController.saveAddress(address, result, model));
    }

    /**
     * Tests saveAddress method with binding error.
     */
    @Test
    void testSaveAddressWithBindingError() {
        when(result.hasErrors()).thenReturn(true);
        assertEquals("add-address", addressController.saveAddress(createAddress(), result, model));
    }

    /**
     * Tests updateAddress method.
     */
    @Test
    void testUpdateAddress() {
        Address address = createAddress();
        when(addressService.getAddressById(1)).thenReturn(address);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("update-address", addressController.updateAddress(1, model));
    }

    /**
     * Tests updateAddress method with null address.
     */
    @Test
    void testUpdateAddressWithNullAddress() {
        when(addressService.getAddressById(1)).thenReturn(null);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-404", addressController.updateAddress(1, model));
    }

    /**
     * Tests updateAddress method with address, which doesn't belong to current user.
     */
    @Test
    void testUpdateAddressWithError403() {
        Address address = createAddress();
        address.getUser().setId(2);
        when(addressService.getAddressById(1)).thenReturn(address);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-403", addressController.updateAddress(1, model));
    }

    /**
     * Tests saveUpdatedAddress method.
     */
    @Test
    void testSaveUpdatedAddress() {
        Address address = createAddress();
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        when(addressService.saveAddress(address)).thenReturn(address);
        assertEquals("addresses", addressController.saveUpdatedAddress(1, address, result, model));
    }

    /**
     * Tests saveUpdatedAddress method with binding error.
     */
    @Test
    void testSaveUpdatedAddressWithBindingError() {
        when(result.hasErrors()).thenReturn(true);
        assertEquals("update-address", addressController.saveUpdatedAddress(1, createAddress(), result, model));
    }

    /**
     * Tests deleteAddress method.
     */
    @Test
    void testDeleteAddress() {
        when(addressService.getAddressById(1)).thenReturn(createAddress());
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("addresses", addressController.deleteAddress(1, model));
    }

    /**
     * Tests deleteAddress method with null address.
     */
    @Test
    void testDeleteAddressWithNullAddress() {
        when(addressService.getAddressById(1)).thenReturn(null);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-404", addressController.deleteAddress(1, model));
    }

    /**
     * Tests deleteAddress method with address, which doesn't belong to current user.
     */
    @Test
    void testDeleteAddressWithError403() {
        Address address = createAddress();
        address.getUser().setId(2);
        when(addressService.getAddressById(1)).thenReturn(address);
        when(userAuthentication.getCurrentUser()).thenReturn(createUser());
        assertEquals("errors/error-403", addressController.deleteAddress(1, model));
    }

    /**
     * Helper method for user creation.
     * @return new user
     */
    public User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        user.setSurname("Smith");
        user.setPassword("password");
        user.setEmail("e@example.com");
        Role role = new Role(1, "TUTOR");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        user.setAddresses(new HashSet<>());
        return user;
    }

    /**
     * Helper method for address creation.
     * @return new address
     */
    public Address createAddress() {
        Address address = new Address();
        address.setId(1);
        address.setCountry("Latvia");
        address.setCity("Riga");
        address.setStreet("Slokas");
        address.setStreetNumber("1a");
        address.setUser(createUser());
        return address;
    }
}
