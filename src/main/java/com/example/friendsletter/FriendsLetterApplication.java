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
        //todo single read fix bag

        //todo popular messages

        SpringApplication.run(FriendsLetterApplication.class, args);
    }

}
