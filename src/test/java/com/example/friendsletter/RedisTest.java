package com.example.friendsletter;

import redis.clients.jedis.JedisPooled;

public class RedisTest {

    public static void main(String[] args) {
        JedisPooled jedis = new JedisPooled("localhost", 6379);
        jedis.set("foo", "bar");
        System.out.println(jedis.get("foo")); // prints "bar"
        System.out.println(jedis.keys("*"));
        System.out.println(jedis.type("letter::5ebd92"));
        System.out.println(jedis.get("letter::5ebd92"));

    }
}
