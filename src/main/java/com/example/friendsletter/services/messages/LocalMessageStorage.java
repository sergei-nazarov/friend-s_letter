package com.example.friendsletter.services.messages;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Profile("dev")
public class LocalMessageStorage implements MessageStorage {
    @Value("${messages.store.folder:messages}")
    public Path localPath;

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(this.localPath);
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
    public InputStream readAsStream(String fileId) throws FileNotFoundException {
        Path filePath = localPath.resolve(fileId);
        try {
            return Files.newInputStream(filePath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new FileNotFoundException(filePath.toString()); //todo handle file not found
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
