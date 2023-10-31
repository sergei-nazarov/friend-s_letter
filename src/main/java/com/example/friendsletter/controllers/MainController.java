package com.example.friendsletter.controllers;

import com.example.friendsletter.data.LetterDto;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.services.LetterService;
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

import java.util.TimeZone;

@Controller
@Slf4j
public class MainController {

    LetterService letterService;

    @Autowired
    public MainController(LetterService letterService) {
        this.letterService = letterService;
    }

    @GetMapping("/")
    String mainPage(Model model, TimeZone timezone) {
        LetterDto letterDto = new LetterDto();
        letterDto.setTimezone(timezone.getID());
        model.addAttribute("letter", letterDto);
        return "index";
    }

    @PostMapping
    String saveLetter(@ModelAttribute("letter") @Valid LetterDto letterDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        LetterDto letter = letterService.saveLetter(letterDto);
        model.addAttribute("letter", letter);
        System.out.println(letter);
        return "letter_created";
    }

    @GetMapping("/l/{letterShortCode}")
    String readLetter(@PathVariable("letterShortCode") String letterShortCode, Model model)
            throws LetterNotAvailableException {

        LetterDto letterDto = letterService.readLetter(letterShortCode);
        model.addAttribute("letter", letterDto);
        return "letter";
    }

    @ModelAttribute("requestURI")
    String getRequestServletPath() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    }

}
