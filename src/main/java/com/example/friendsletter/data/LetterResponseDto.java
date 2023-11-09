package com.example.friendsletter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object for displaying the letter
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterResponseDto {

    private String message;
    private String letterShortCode;
    private LocalDateTime created;
    private LocalDateTime expirationDate;
    private String title;
    private String author;
    private boolean singleRead;
    private boolean publicLetter;
}
