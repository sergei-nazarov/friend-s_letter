package com.example.friendsletter.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object for displaying the letter with count of visits
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularLetterResponseDto {

    private String message;
    private String letterShortCode;
    private LocalDateTime created;
    private LocalDateTime expirationDate;
    private String title;
    private String author;
    private boolean singleRead;
    private boolean publicLetter;
    private long countVisits;
    @JsonIgnore
    private String messageId;

    public PopularLetterResponseDto(LetterMetadata letter, long countVisits) {
        this.letterShortCode = letter.getLetterShortCode();
        this.created = letter.getCreated();
        this.expirationDate = letter.getExpirationDate();
        this.title = letter.getTitle();
        this.author = letter.getAuthor();
        this.singleRead = letter.isSingleRead();
        this.publicLetter = letter.isPublicLetter();
        this.messageId = letter.getMessageId();
        this.countVisits = countVisits;
    }
}
