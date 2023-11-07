package com.example.friendsletter.services.Cache;

import com.example.friendsletter.data.MessageCacheDto;
import com.example.friendsletter.repository.RedisMessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Redis cache handler
 */
@Component
@Profile({"prod", "prod-docker"})
public class RedisMessageCache implements MessageCache {
    private final RedisMessageRepository redisMessageRepository;

    public RedisMessageCache(RedisMessageRepository redisMessageRepository) {
        this.redisMessageRepository = redisMessageRepository;
    }

    @Override
    public Optional<MessageCacheDto> get(String letterShortCode) {
        return redisMessageRepository.findById(letterShortCode);
    }

    @Override
    public void save(MessageCacheDto message) {
        redisMessageRepository.save(message);
    }
}
