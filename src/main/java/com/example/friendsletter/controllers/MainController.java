package com.example.friendsletter.controllers;

import com.example.friendsletter.data.*;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.services.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    String mainPage(Model model, Authentication authentication) {

        LetterRequestDto letterDto = new LetterRequestDto();
        letterDto.setPublicLetter(true);

        if (authentication != null) {
            letterDto.setAuthor(authentication.getName());
        }
        model.addAttribute("letter", letterDto);
        return "index";

    }

    @GetMapping("/about")
    String aboutPage() {
        return "about";

    }

    @PostMapping
    String saveLetter(@ModelAttribute("letter") @Valid LetterRequestDto letterDto,
                      BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        User user = null;
        if (authentication != null) {
            user = (User) authentication.getPrincipal();
        }
        LetterResponseDto letter = letterService.saveLetter(user, letterDto);
        model.addAttribute("letter", letter);
        return "letter_created";
    }

    @GetMapping("/u/{letterShortCode}")
    String getLetterForUpdate(@PathVariable String letterShortCode, Model model, Authentication authentication) throws LetterNotAvailableException {
        if (authentication == null) {
            return "redirect:/l/" + letterShortCode;
        }
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || letterService.doesUserOwnLetter(user, letterShortCode)) {
            LetterResponseDto letterResponseDto = letterService.readLetter(letterShortCode, false);
            model.addAttribute("letter", letterResponseDto);
            return "letter_update";
        } else {
            return "redirect:/l/" + letterShortCode;
        }
    }

    @PostMapping("/u/{letterShortCode}")
    String updateLetter(@ModelAttribute("letter") @Valid LetterRequestDto letterDto,
                        BindingResult bindingResult, Model model,
                        @PathVariable("letterShortCode") String letterShortCode,
                        Authentication authentication) throws LetterNotAvailableException {
        if (authentication == null) {
            return "redirect:/l/" + letterShortCode;
        }
        User user = (User) authentication.getPrincipal();
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !letterService.doesUserOwnLetter(user, letterShortCode)) {
            return "redirect:/l/" + letterShortCode;
        }
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
                      Model model, HttpServletRequest request, Authentication authentication)
            throws LetterNotAvailableException {
        LetterResponseDto letterResponseDto = letterService.readLetter(letterShortCode);
        letterService.writeVisit(letterShortCode, request.getRemoteAddr());
        model.addAttribute("letter", letterResponseDto);
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || letterService.doesUserOwnLetter(user, letterShortCode)) {
                model.addAttribute("itIsOwner", true);
            }
        }
        return "letter";
    }

    @GetMapping("/person/letters")
    public String personLetters(Authentication authentication,
                                Model model) {
        List<LetterResponseDto> letters = letterService.getLettersByUser(
                (User) authentication.getPrincipal(), PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "created")));
        letters.forEach(x -> x.setMessage(cutShortStringForView(x.getMessage(), 200)));
        model.addAttribute("letters", letters);
        return "person_letters";
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
    UserDto getUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return ((User) authentication.getPrincipal()).toUserDto();
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
