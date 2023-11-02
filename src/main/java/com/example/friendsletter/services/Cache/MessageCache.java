package com.example.friendsletter.services.Cache;

import com.example.friendsletter.data.MessageCacheDto;

import java.util.Optional;

public interface MessageCache {
    Optional<MessageCacheDto> get(String shortLetterCode);

    void set(MessageCacheDto message);
}
