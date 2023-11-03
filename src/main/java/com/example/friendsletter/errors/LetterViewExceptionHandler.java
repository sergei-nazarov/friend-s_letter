package com.example.friendsletter.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@ControllerAdvice
public class LetterViewExceptionHandler {

    MessageSource messageSource;

    @Autowired
    public LetterViewExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(LetterNotAvailableException.class)
    public String handleException(LetterNotAvailableException e, RedirectAttributes redirectAttributes) {
        LETTER_ERROR_STATUS status = e.getStatus();
        String key;
        switch (status) {
            case NOT_FOUND -> key = "read.error.not_found";
            case NOT_PUBLIC -> key = "read.error.not_public";
            case EXPIRED -> key = "read.error.expired";
            case HAS_BEEN_READ -> key = "read.error.has_been_read";
            case MESSAGE_NOT_FOUND -> key = "read.error.message_not_found";
            default -> throw new RuntimeException("Unexpected letter exception code:" + status);
        }
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage(key, new String[]{e.getLetterShortCode()}, locale);
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleIOException(NoHandlerFoundException error, RedirectAttributes redirectAttributes) {
        System.out.println(123);
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.not_found", new String[]{error.getRequestURL()}, locale);
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/";
    }

}
