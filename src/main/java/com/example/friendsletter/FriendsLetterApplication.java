package com.example.friendsletter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
