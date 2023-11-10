package com.example.friendsletter.services.messages;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Class for storing, reading, updating and deleting messages
 */
public interface MessageStorage {
    /**
     * Save the message in message store
     *
     * @param message message to save
     * @return unique file id
     */
    String save(InputStream message);

    /**
     * Update the message in message store
     *
     * @param fileId  - file id to update
     * @param message - new message
     * @return true - succeed, false - failure
     */
    boolean update(String fileId, InputStream message);

    /**
     * Read the message from message store
     *
     * @param fileId file id to read
     * @return message as an input stream
     */
    InputStream readAsStream(String fileId) throws FileNotFoundException;

    /**
     * Delete the message from message store
     *
     * @param fileId file id to delete
     * @return result of operation
     */
    boolean delete(String fileId);

    /**
     * Get all files from message store
     *
     * @return list of all files
     */
    List<String> getAllFiles();

    /**
     * Convenient method to save the message
     *
     * @param message message to save
     * @return unique file id
     */
    default String save(String message) {
        return save(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Convenient method to update the message
     *
     * @param fileId  - file id to update
     * @param message - new message
     * @return true - succeed, false - failure
     */
    default boolean update(String fileId, String message) {
        return update(fileId, new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Convenient method to read the message
     *
     * @param fileId file id to read
     * @return message as a text
     * @throws FileNotFoundException - message file not found.
     * @throws RuntimeException      - any other errors
     */
    default String read(String fileId) throws FileNotFoundException {
        try {
            return new String(readAsStream(fileId).readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
