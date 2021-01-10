package com.tutoring.portal.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static com.tutoring.portal.util.CommonConstants.ERROR_403_VIEW;
import static com.tutoring.portal.util.CommonConstants.ERROR_404_VIEW;

@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Returns error view which matches request's status code.
     * If request's status code doesn't match any views, returns generic error view.
     *
     * @param request a HttpServletRequest object which provides access to the request information
     * @return error-403 view or error-404 view or error-500 view or error view
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return ERROR_403_VIEW;
            }
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ERROR_404_VIEW;
            }
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/error-500";
            }
        }
        return "errors/error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
