package com.example.friendsletter.controllers;


import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserRegistrationDto;
import com.example.friendsletter.services.UserService;
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
    private final UserService userService;

    @Autowired
    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/";
    }

}
