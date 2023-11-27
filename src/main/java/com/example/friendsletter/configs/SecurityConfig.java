package com.example.friendsletter.configs;

import com.example.friendsletter.services.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private static final String LAST_LOGIN_USERNAME = "LAST_LOGIN_USERNAME";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(
                                new AntPathRequestMatcher("/actuator/health")
                        ).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/actuator"),
                                new AntPathRequestMatcher("/actuator/**"),
                                new AntPathRequestMatcher("/api1/boost")
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                new AntPathRequestMatcher("/u/**")
                        ).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(
                                new AntPathRequestMatcher("/person/**"),
                                new AntPathRequestMatcher("/person")
                        )
                        .hasRole("USER")
                        .anyRequest()
                        .permitAll()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .successHandler(((request, response, authentication) -> new SimpleUrlAuthenticationFailureHandler("/") {
                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                        super.onAuthenticationFailure(request, response, exception);
                                        try {
                                            HttpSession session = request.getSession(false);
                                            if (session != null) {
                                                request.getSession().removeAttribute(LAST_LOGIN_USERNAME);
                                            }
                                        } catch (IllegalStateException illegalStateException) {
                                            log.warn(Arrays.toString(illegalStateException.getStackTrace()));
                                        }
                                    }
                                }))
                                .defaultSuccessUrl("/")
                                .failureHandler((request, response, exception) -> new SimpleUrlAuthenticationFailureHandler("/login?error") {
                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                        super.onAuthenticationFailure(request, response, exception);
                                        String lastUserName = request.getParameter("username");
                                        // Store the given username in the session for accessing in thymeleaf
                                        try {
                                            HttpSession session = request.getSession(false);
                                            if (session != null) {
                                                request.getSession().setAttribute(LAST_LOGIN_USERNAME, lastUserName);
                                            }
                                        } catch (IllegalStateException illegalStateException) {
                                            log.warn(Arrays.toString(illegalStateException.getStackTrace()));
                                        }
                                    }
                                }.onAuthenticationFailure(request, response, exception))
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .clearAuthentication(true)
                                .logoutSuccessUrl("/login")
                                .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                //for h2 db
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }


}

