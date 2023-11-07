package com.example.friendsletter.services.Cache;

import com.example.friendsletter.data.MessageCacheDto;

import java.util.Optional;

/**
 * Interface for different message cache classes
 */
public interface MessageCache {
    Optional<MessageCacheDto> get(String shortLetterCode);

    void save(MessageCacheDto message);
}
