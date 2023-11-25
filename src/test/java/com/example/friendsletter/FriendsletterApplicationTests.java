package com.example.friendsletter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@ActiveProfiles("test")
class FriendsletterApplicationTests {

    @Test
    void contextLoads() {
    }

}
