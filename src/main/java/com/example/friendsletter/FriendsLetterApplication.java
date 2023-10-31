package com.example.friendsletter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
public class FriendsLetterApplication {

    public static void main(String[] args) {

        //todo add local compressed message store
        //todo localization en/fr/ru
        //todo tests
        //todo message qr code
        //todo list public messages
        //todo redirect after post
        //todo rate limiter
        //todo logs

        //local timezone in view
        //message stats //ready
        //copy letter button //ready


        SpringApplication.run(FriendsLetterApplication.class, args);
    }


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setCacheSeconds(10); //reload messages every 10 seconds
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
