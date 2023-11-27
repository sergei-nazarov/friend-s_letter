package com.example.friendsletter.controllers;


import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserDto;
import com.example.friendsletter.errors.UserUpdateException;
import com.example.friendsletter.services.CustomUserDetailsService;
import com.example.friendsletter.services.LetterService;
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
    private final LetterService letterService;

    private final AuthenticationProvider authenticationProvider;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    @Autowired
    public AuthorizationController(CustomUserDetailsService userService, MessageSource messageSource,
                                   LetterService letterService, AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.letterService = letterService;
        this.authenticationProvider = authenticationProvider;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("userDto", user);
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("userDto", user);
        return "login";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("userDto") UserDto userDto,
                               BindingResult result,
                               Model model, Locale locale, HttpServletRequest request,
                               HttpServletResponse response) {
        if (userDto.getPassword() == null || userDto.getPassword().length() == 0) {
            result.rejectValue("password", null,
                    messageSource.getMessage("registration.error.empty_password", null, locale));
        }
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

    @GetMapping("/person/update")
    public String personalPage() {
        return "person";
    }


    @PostMapping("/person/update")
    public String updateUserInfo(@Valid @ModelAttribute("user") UserDto userDto,
                                 BindingResult bindingResult,
                                 Authentication authentication
    ) throws UserUpdateException {
        if (bindingResult.hasErrors()) {
            return "person";
        }
        User user = (User) authentication.getPrincipal();
        userService.updateUser(user, userDto);
        return "redirect:/person/update";
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

}
