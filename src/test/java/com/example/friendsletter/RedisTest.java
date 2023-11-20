package com.example.friendsletter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RedisTest {

    public static void main(String[] args) throws IOException, InterruptedException {
//        JedisPooled jedis = new JedisPooled("localhost", 6379);
//        jedis.set("foo", "bar");
//        System.out.println(jedis.get("foo")); // prints "bar"
//        System.out.println(jedis.keys("*"));
//        System.out.println(jedis.type("letter::5ebd92"));
//        System.out.println(jedis.get("letter::5ebd92"));

        String url = "https://iss.moex.com/iss/rms/engines/currency/objects/centralrates.json?date=2023-11-14&iss.json=extended";
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest.Builder get = HttpRequest.newBuilder(URI.create(url)).method("GET", HttpRequest.BodyPublishers.noBody());
        HttpResponse<String> send = client.send(get.build(), HttpResponse.BodyHandlers.ofString());
        String body = send.body();
        System.out.println(body);
        for (char c : body.toCharArray()) {
            System.out.println((int) c + " " + c);
        }
    }
}
