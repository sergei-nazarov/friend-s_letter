package com.example.friendsletter.controllers;


import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserRegistrationDto;
import com.example.friendsletter.services.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;


@Controller
@Slf4j
public class AuthorizationController {
    private final CustomUserDetailsService userService;
    private final MessageSource messageSource;
    private final AuthenticationProvider authenticationProvider;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    @Autowired
    public AuthorizationController(CustomUserDetailsService userService, MessageSource messageSource,
                                   AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.authenticationProvider = authenticationProvider;
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
                               Model model, Locale locale, HttpServletRequest request,
                               HttpServletResponse response) {

        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "/register";
        }

        User existingUser = userService.loadUserByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && existingUser.getEmail().equals(userDto.getEmail())) {
            result.rejectValue("email", null,
                    messageSource.getMessage("registration.error.email_exists", null, locale));
        }
        if (existingUser != null && existingUser.getUsername() != null && existingUser.getUsername().equals(userDto.getUsername())) {
            result.rejectValue("username", null,
                    messageSource.getMessage("registration.error.username_exists", null, locale));
        }
        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "/register";
        }
        User user = userService.saveUser(userDto);
        log.info("New registration: " + user);
        doAutoLogin(user.getUsername(), userDto.getPassword(), request, response);
        return "redirect:/";
    }

    private void doAutoLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                    username, password);
            Authentication authentication = authenticationProvider.authenticate(token);
            SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);
        } catch (Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            log.error("Failure in autoLogin", e);
        }
    }
}
