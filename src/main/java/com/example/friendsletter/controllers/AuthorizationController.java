package com.example.friendsletter.controllers;


import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserRegistrationDto;
import com.example.friendsletter.services.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthorizationController {
    private final CustomUserDetailsService userService;

    @Autowired
    public AuthorizationController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("userDto", user);
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("userDto", user);
        return "login";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "/register";
        }

        User existingUser = userService.loadUserByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && existingUser.getEmail().equals(userDto.getEmail())) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }
        if (existingUser != null && existingUser.getUsername() != null && existingUser.getUsername().equals(userDto.getUsername())) {
            result.rejectValue("username", null,
                    "There is already an account registered with the same username");
        }

        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/";
    }

}
