package com.example.friendsletter.services.Cache;

import com.example.friendsletter.data.MessageCacheDto;
import com.example.friendsletter.repository.RedisMessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

@Component
@Profile({"prod", "prod-docker"})
public class RedisMessageCache implements MessageCache {
    private final RedisMessageRepository redisMessageRepository;

    public RedisMessageCache(RedisMessageRepository redisMessageRepository) {
        this.redisMessageRepository = redisMessageRepository;
    }

    public static void main(String[] args) {
        JedisPool pool = new JedisPool("localhost", 6379);
        try (Jedis jedis = pool.getResource()) {
            for (String key : jedis.keys("*")) {
                System.out.println(key + " " + jedis.type(key));
                if (jedis.type(key).equals("hash")) {
                    System.out.println(jedis.hgetAll(key));
                }
            }
            System.out.println(jedis.smembers("messages"));
        }
    }

    @Override
    public Optional<MessageCacheDto> get(String letterShortCode) {
        return redisMessageRepository.findById(letterShortCode);
    }

    @Override
    public void set(MessageCacheDto message) {
        redisMessageRepository.save(message);
    }
}
