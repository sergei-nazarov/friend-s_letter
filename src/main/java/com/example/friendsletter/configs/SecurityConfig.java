package com.example.friendsletter.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${actuator.login}")
    private String login;
    @Value("${actuator.password}")
    private String password;

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
                                new AntPathRequestMatcher("/actuator/**")
                        )
                        .authenticated()
                        .anyRequest()
                        .permitAll()
                ).httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                //for h2 db
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username(login)
                .password(passwordEncoder().encode(password))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}

