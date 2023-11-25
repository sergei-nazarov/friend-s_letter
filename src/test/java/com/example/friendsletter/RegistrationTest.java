package com.example.friendsletter;


import com.example.friendsletter.controllers.AuthorizationController;
import com.example.friendsletter.data.User;
import com.example.friendsletter.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RegistrationTest {

    private final MockMvc mockMvc;
    private final AuthorizationController controller;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;


    @Autowired
    public RegistrationTest(MockMvc mockMvc, AuthorizationController controller, UserRepository userRepository, ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.controller = controller;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Test
    void simpleRegistrationAndLoginTest() throws Exception {
        String email = "example@example.com";
        String username = "Abas";
        String password = "12345";

        mockMvc.perform(
                        post("/register").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("email", email)
                                .param("username", username)
                                .param("password", password)
                )
                .andExpect(status().is3xxRedirection());

        User byEmail = userRepository.findByEmail(email);
        Assertions.assertEquals(byEmail.getUsername(), username);

        User byUsername = userRepository.findByUsername(username);
        Assertions.assertEquals(byUsername.getEmail(), email);

        User firstByEmail = userRepository.findFirstByUsernameOrEmail(email, email);
        Assertions.assertEquals(firstByEmail.getUsername(), username);

        User firstByUsername = userRepository.findFirstByUsernameOrEmail(username, username);
        Assertions.assertEquals(firstByUsername.getEmail(), email);

        ResultActions resultActions = mockMvc.perform(
                        post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                )
                .andDo(print()).andExpect(status().is3xxRedirection());
        Object security = resultActions.andReturn().getRequest().getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        Assertions.assertNotNull(security);
        Assertions.assertEquals(username, ((SecurityContextImpl) security).getAuthentication().getName());
    }

}
