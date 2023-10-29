package com.example.friendsletter.messages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class LocalMessageStorage implements MessageStorage {
    public Path localPath = Paths.get("storage");

    public LocalMessageStorage() {
        try {
            Files.createDirectories(localPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String save(InputStream message) {
        String fileId;
        Path filePath;
        do {
            fileId = UUID.randomUUID().toString();
            filePath = localPath.resolve(fileId);
        } while (Files.exists(filePath));

        try {
            Files.write(filePath, message.readAllBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileId;
    }

    @Override
    public boolean update(String fileId, InputStream message) {
        Path filePath = localPath.resolve(fileId);
        if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.write(filePath, message.readAllBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public InputStream readAsStream(String fileId) {
        Path filePath = localPath.resolve(fileId);
        try {
            return Files.newInputStream(filePath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e); //todo handle file not found
        }
    }

    @Override
    public boolean delete(String fileId) {
        Path filePath = localPath.resolve(fileId);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAllFiles() {
        try (Stream<Path> walk = Files.walk(localPath, 1, FileVisitOption.FOLLOW_LINKS)) {
            return walk.map(x -> x.getFileName().toString()).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
