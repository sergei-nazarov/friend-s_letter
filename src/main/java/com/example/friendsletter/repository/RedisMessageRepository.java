package com.example.friendsletter.repository;

import com.example.friendsletter.data.MessageCacheDto;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Redis cache
 */
@Repository
@Profile({"prod", "prod-docker"})
public interface RedisMessageRepository extends CrudRepository<MessageCacheDto, String> {
}
