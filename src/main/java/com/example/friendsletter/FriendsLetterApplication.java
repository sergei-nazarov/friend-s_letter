package com.example.friendsletter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FriendsLetterApplication {

    public static void main(String[] args) {

        //todo add local compressed message store
        //todo tests
        //todo message qr code
        //todo redirect after post
        //todo rate limiter
        //todo single use bag

        //list public messages
        //cache!!
        //swagger
        //api endpoints
        //local timezone in view
        //message stats
        //copy letter button
        //localization en/fr/ru
        //javadoc
        //404 handle
        //different dto for create message
        //docker and redis and postgres
        SpringApplication.run(FriendsLetterApplication.class, args);
    }

}
