package com.example.friendsletter.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class LetterViewExceptionHandler {

    MessageSource messageSource;

    @Autowired
    public LetterViewExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(LetterNotAvailableException.class)
    public String handleException(LetterNotAvailableException e, RedirectAttributes redirectAttributes) {
        int errorCode = e.getErrorCode();
        String key;
        switch (errorCode) {
            case LetterNotAvailableException.NOT_FOUND -> key = "read.error.not_found";
            case LetterNotAvailableException.NOT_PUBLIC -> key = "read.error.not_public";
            case LetterNotAvailableException.EXPIRED -> key = "read.error.expired";
            case LetterNotAvailableException.HAS_BEEN_READ -> key = "read.error.has_been_read";
            default -> throw new RuntimeException("Unexpected letter exception code:" + errorCode);
        }
        String errorMessage = messageSource.getMessage(key, new String[]{e.getLetterShortCode()}, null);
        redirectAttributes.addFlashAttribute("getLetterErrorMessage", errorMessage);
        return "redirect:/";
    }

}
