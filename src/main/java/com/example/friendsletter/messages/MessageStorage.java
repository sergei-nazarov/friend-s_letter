package com.example.friendsletter.messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface MessageStorage {
    String save(InputStream message);

    boolean update(String fileId, InputStream message);

    InputStream readAsStream(String fileId);

    boolean delete(String fileId);

    List<String> getAllFiles();

    default String save(String message) {
        return save(new ByteArrayInputStream(message.getBytes()));
    }

    default boolean update(String fileId, String message) {
        return update(fileId, new ByteArrayInputStream(message.getBytes()));
    }

    default String read(String fileId) {
        try {
            return new String(readAsStream(fileId).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
