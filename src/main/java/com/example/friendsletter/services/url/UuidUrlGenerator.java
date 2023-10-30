package com.example.friendsletter.services.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidUrlGenerator implements UrlGenerator {

    @Value("${messages.url.length:8}")
    int length;

    @Override
    public String generate() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return length >= uuid.length() ? uuid : uuid.substring(0, length);
    }
}
