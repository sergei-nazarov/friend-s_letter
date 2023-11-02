package com.example.friendsletter.services.Cache;

import com.example.friendsletter.data.MessageCacheDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("dev")
public class NoMessageCache implements MessageCache {


    @Override
    public Optional<MessageCacheDto> get(String shortLetterCode) {
        return Optional.empty();
    }

    @Override
    public void set(MessageCacheDto message) {
        //do nothing
    }
}
