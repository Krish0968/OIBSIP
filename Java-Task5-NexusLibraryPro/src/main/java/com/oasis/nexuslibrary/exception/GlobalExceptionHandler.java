package com.oasis.nexuslibrary.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        logger.error("Resource not found exception: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException ex, RedirectAttributes redirectAttributes) {
        logger.error("Bad request exception: {}", ex.getMessage());
        // For BadRequests, we usually want to redirect back to the previous page and show a Toast warning
        redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        return "redirect:/"; // Controllers will typically handle the return mapping or redirect appropriately
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        logger.error("Unhandle system exception: ", ex);
        model.addAttribute("errorMessage", "An unexpected system error occurred. Please contact the administrator.");
        return "error/500";
    }
}
