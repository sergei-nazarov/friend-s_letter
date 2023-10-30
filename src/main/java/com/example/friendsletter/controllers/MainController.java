package com.example.friendsletter.controllers;

import com.example.friendsletter.data.LetterDto;
import com.example.friendsletter.services.LetterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.TimeZone;

@Controller
public class MainController {

    LetterService letterService;

    @Autowired
    public MainController(LetterService letterService) {
        this.letterService = letterService;
    }

    @GetMapping
    String mainPage(Model model, TimeZone timezone) {
        LetterDto letterDto = new LetterDto();
        letterDto.setTimezone(timezone.getID());
        model.addAttribute("letter", letterDto);
        return "index";
    }

    @PostMapping
    String processLetter(@ModelAttribute("letter") @Valid LetterDto letterDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        letterService.saveLetter(letterDto);
        return "redirect:/";
    }


}
