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

import static com.tutoring.portal.util.CommonConstants.ADDRESSES;
import static com.tutoring.portal.util.CommonConstants.ADDRESSES_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_403_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_404_VIEW;
import static com.tutoring.portal.util.CommonConstants.SUCCESS_MESSAGE;

@Controller
public class AddressController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserAuthentication userAuthentication;

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    /**
     * Returns view with all user's addresses.
     *
     * @param model a Model object used in the view
     * @return addresses view
     */
    @GetMapping(value = "addresses")
    public String getUserAddresses(Model model) {
        User user = userAuthentication.getCurrentUser();
        model.addAttribute(ADDRESSES, user.getAddresses());
        return ADDRESSES_VIEW;
    }

    /**
     * Returns view with new address creation form.
     *
     * @param model a Model object used in the view
     * @return add-address view
     */
    @GetMapping(value = "addresses/add")
    public String addAddress(Model model) {
        Address address = new Address();
        address.setUser(userAuthentication.getCurrentUser());
        model.addAttribute("address", address);
        return "add-address";
    }

    /**
     * Validates provided address, saves it and returns view with user's addresses.
     * If provided address is invalid, returns view with new address creation form.
     *
     * @param address new address
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return addresses view or add-address view, if provided address is invalid
     */
    @PostMapping(value = "addresses/add")
    public String saveAddress(@Valid Address address, BindingResult result, Model model) {
        // check if address is invalid
        if (result.hasErrors()) {
            logger.error("Cannot save address, wrong input");
            return "add-address";
        }

        addressService.saveAddress(address);
        logger.info("Address successfully saved");

        User user = userAuthentication.getCurrentUser();
        model.addAttribute(ADDRESSES, user.getAddresses());
        model.addAttribute(SUCCESS_MESSAGE, "Successfully added new address");
        return ADDRESSES_VIEW;
    }

    /**
     * Returns view with address update form if provided ID is valid.
     * Otherwise returns error-404 or error-403 view.
     *
     * @param id address ID
     * @param model a Model object used in the view
     * @return update-address view or error view, if error occurred
     */
    @GetMapping(value = "addresses/update/{id}")
    public String updateAddress(@PathVariable int id, Model model) {
        Address address = addressService.getAddressById(id);
        User user = userAuthentication.getCurrentUser();
        // return error-404 view if address with provided ID doesn't exist
        if (address == null) {
            return ERROR_404_VIEW;
        }
        // return error-403 view if address with provided ID doesn't belong to user
        if (address.getUser().getId() != user.getId()) {
            return ERROR_403_VIEW;
        }
        model.addAttribute("address", address);
        return "update-address";
    }

    /**
     * Validates provided address, saves it and returns view with user's addresses.
     * If provided address is invalid, returns view with address update form.
     *
     * @param id address ID
     * @param address updated address
     * @param result a BindingResult object that holds the result of the validation and binding
     * @param model a Model object used in the view
     * @return addresses view or update-address view, if provided address is invalid
     */
    @PostMapping(value = "addresses/update/{id}")
    public String saveUpdatedAddress(@PathVariable int id, @Valid Address address, BindingResult result, Model model) {
        // check if address is invalid
        if (result.hasErrors()) {
            logger.error("Cannot update address, wrong input");
            return "update-address";
        }
        addressService.saveAddress(address);
        logger.info("Address successfully updated");

        model.addAttribute(SUCCESS_MESSAGE, "Address was successfully updated");
        model.addAttribute(ADDRESSES, userAuthentication.getCurrentUser().getAddresses());
        return ADDRESSES_VIEW;
    }

    /**
     * Deletes address with provided ID and returns view with user's addresses.
     * If address with provided ID doesn't exist, returns error-404 view.
     * If address with provided ID doesn't belong to user, returns error-404 view.
     *
     * @param id address ID
     * @param model a Model object used in the view
     * @return addresses view or error view, if error occurred
     */
    @GetMapping(value = "addresses/delete/{id}")
    public String deleteAddress(@PathVariable int id, Model model) {
        User user = userAuthentication.getCurrentUser();
        // check if address exists
        if (addressService.getAddressById(id) == null) {
            return ERROR_404_VIEW;
        }
        // check if address belongs to user
        if (addressService.getAddressById(id).getUser().getId() != user.getId()) {
            return ERROR_403_VIEW;
        }

        addressService.deleteAddress(id);
        String message = "Successfully deleted address with ID: " + id;
        logger.info(message);
        model.addAttribute(SUCCESS_MESSAGE, message);
        model.addAttribute(ADDRESSES, user.getAddresses());
        return ADDRESSES_VIEW;
    }
}
