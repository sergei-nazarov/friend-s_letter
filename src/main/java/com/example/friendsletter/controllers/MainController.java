package com.example.friendsletter.controllers;

import com.example.friendsletter.data.LetterRequestDto;
import com.example.friendsletter.data.LetterResponseDto;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.services.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * View controller
 */
@Controller
@Slf4j
public class MainController {

    LetterService letterService;

    @Autowired
    public MainController(LetterService letterService) {
        this.letterService = letterService;
    }

    @GetMapping("/")
    String mainPage(Model model) {
        LetterResponseDto letterDto = new LetterResponseDto();
        model.addAttribute("letter", letterDto);
        return "index";

    }

    @PostMapping
    String saveLetter(@ModelAttribute("letter") @Valid LetterRequestDto letterDto,
                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        LetterResponseDto letter = letterService.saveLetter(letterDto);
        model.addAttribute("letter", letter);
        return "letter_created";
    }

    @GetMapping("/l/{letterShortCode}")
    String readLetter(@PathVariable("letterShortCode") String letterShortCode,
                      Model model, HttpServletRequest request)
            throws LetterNotAvailableException {
        LetterResponseDto letterResponseDto = letterService.readLetter(letterShortCode);
        letterService.writeVisit(letterShortCode, request.getRemoteAddr());
        model.addAttribute("letter", letterResponseDto);
        return "letter";
    }

    /**
     * @return current HOST name
     */
    @ModelAttribute("serverHost")
    String getRequestServletPath() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    }

}
