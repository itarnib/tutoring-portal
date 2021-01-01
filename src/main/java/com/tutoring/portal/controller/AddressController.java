package com.tutoring.portal.controller;

import com.tutoring.portal.model.Address;
import com.tutoring.portal.model.User;
import com.tutoring.portal.service.AddressService;
import com.tutoring.portal.service.UserService;
import com.tutoring.portal.util.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AddressController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @GetMapping(value = "addresses")
    public String getUserAddresses(Model model) {
        User user = userAuthentication.getCurrentUser();
        model.addAttribute("addresses", user.getAddresses());
        return "addresses";
    }

    @GetMapping(value = "addresses/add")
    public String addAddress(Address address, Model model) {
        address.setUser(userAuthentication.getCurrentUser());
        model.addAttribute("address", address);
        return "add-address";
    }

    @PostMapping(value = "addresses/add")
    public String saveAddress(@Valid Address address, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot save address, wrong input");
            return "add-address";
        }
        addressService.saveAddress(address);
        logger.info("Address successfully saved");

        User user = userAuthentication.getCurrentUser();
        model.addAttribute("addresses", user.getAddresses());
        model.addAttribute("successMessage", "Successfully added new address");
        return "addresses";
    }

    @GetMapping(value = "addresses/update/{id}")
    public String updateAddress(@PathVariable int id, Model model) {
        Address address = addressService.getAddressById(id);
        User user = userAuthentication.getCurrentUser();
        if (address == null) {
            model.addAttribute("warningMessage", "Address with ID " + id + " does not exist");
            model.addAttribute("addresses", user.getAddresses());
            return "addresses";
        }
        if (address.getUser().getId() != user.getId()) {
            model.addAttribute("warningMessage", "Address with ID " + id + " does not belong to you");
            model.addAttribute("addresses", user.getAddresses());
            return "addresses";
        }
        model.addAttribute("address", address);
        return "update-address";
    }

    @PostMapping(value = "addresses/update/{id}")
    public String saveUpdatedAddress(@PathVariable int id, @Valid Address address, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Cannot update address, wrong input");
            return "update-address";
        }
        addressService.saveAddress(address);
        logger.info("Address successfully updated");

        model.addAttribute("successMessage", "Address was successfully updated");
        model.addAttribute("addresses", userAuthentication.getCurrentUser().getAddresses());
        return "addresses";
    }

    @GetMapping(value = "addresses/delete/{id}")
    public String deleteAddress(@PathVariable int id, Model model) {
        User user = userAuthentication.getCurrentUser();

        if (addressService.getAddressById(id) == null) {
            model.addAttribute("warningMessage", "Address with ID " + id + " does not exist");
        } else if (addressService.getAddressById(id).getUser().getId() != user.getId()) {
            model.addAttribute("warningMessage", "Address with ID " + id + " does not belong to you");
        } else {
            addressService.deleteAddress(id);
            String message = "Successfully deleted address with ID: " + id;
            logger.info(message);
            model.addAttribute("successMessage", "Address with ID " + id + " was successfully deleted");
        }

        model.addAttribute("addresses", user.getAddresses());
        return "addresses";
    }
}
