package com.example.friendsletter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Entity for caching messages in Redis
 */
@Getter
@RedisHash(value = "messages", timeToLive = 120)
@AllArgsConstructor
public class MessageCacheDto {
    @Id
    private final String messageId;
    private final String message;
}
