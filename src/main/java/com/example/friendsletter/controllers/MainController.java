package com.example.friendsletter.controllers;

import com.example.friendsletter.data.LetterRequestDto;
import com.example.friendsletter.data.LetterResponseDto;
import com.example.friendsletter.data.PopularLetterResponseDto;
import com.example.friendsletter.data.User;
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

import java.security.Principal;
import java.util.List;

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
        LetterRequestDto letterDto = new LetterRequestDto();
        letterDto.setPublicLetter(true);
        model.addAttribute("letter", letterDto);
        return "index";

    }

    @GetMapping("/about")
    String aboutPage() {
        return "about";

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

    @GetMapping("/u/{letterShortCode}")
    String getLetterForUpdate(@PathVariable String letterShortCode, Model model) throws LetterNotAvailableException {
        LetterResponseDto letterResponseDto = letterService.readLetter(letterShortCode, false);
        model.addAttribute("letter", letterResponseDto);
        return "letter_update";
    }

    @PostMapping("/u/{letterShortCode}")
    String updateLetter(@ModelAttribute("letter") @Valid LetterRequestDto letterDto,
                        BindingResult bindingResult, Model model,
                        @PathVariable("letterShortCode") String letterShortCode) throws LetterNotAvailableException {
        if (bindingResult.hasErrors()) {
            return "letter_update";
        }
        LetterResponseDto letter = letterService.updateLetter(letterShortCode, letterDto);
        model.addAttribute("letter", letter);
        model.addAttribute("letterShortCode", letterShortCode);
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

    /**
     * @return Popular letters prepared for view
     */
    @ModelAttribute("mostPopularLetters")
    List<PopularLetterResponseDto> getMostPopularMessages() {
        List<PopularLetterResponseDto> popular = letterService.getMostPopular();
        return popular.stream().peek(letter -> {
            letter.setMessage(cutShortStringForView(letter.getMessage(), 200));
            letter.setTitle(cutShortStringForView(letter.getTitle(), 30));
            letter.setAuthor(cutShortStringForView(letter.getAuthor(), 30));
        }).limit(5).toList();
    }

    /**
     * @return user if exists
     */
    @ModelAttribute("user")
    User getUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        System.out.println(principal.getName());

        return null;
    }

    private String cutShortStringForView(String string, int countChars) {
        int stringSize = string.length();
        if (stringSize < countChars) {
            return string.replace("\n", " ");
        } else {
            return string.substring(0, Math.max(0, countChars - 3)).replace("\n", " ") + "...";
        }
    }


}
