package com.example.friendsletter.controllers;

import com.example.friendsletter.data.LetterDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.TimeZone;

@Controller
public class MainController {

    @GetMapping
    String mainPage(Model model, TimeZone timezone) {
        model.addAttribute("letter", new LetterDto());
        model.addAttribute("timezone", timezone.getID());
        return "index";
    }

    @PostMapping("/message")
    String processLetter(@Valid @ModelAttribute LetterDto letterDto, BindingResult errors, TimeZone timezone) {
        System.out.println(errors.getAllErrors());
        System.out.println(letterDto + " " + timezone.getID());
        return "redirect:/";
    }


}
