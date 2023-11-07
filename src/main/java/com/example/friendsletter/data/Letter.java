package com.example.friendsletter.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity for storage in database
 */
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "letters")
public class Letter {

    @Id
    @Column(nullable = false)
    private String letterShortCode;

    @OneToMany(mappedBy = "letter", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    List<LetterStat> visits;
    /**
     * If true, the message can only be read 1 time
     */
    private boolean singleUse;
    /**
     * Can be shown in API
     */
    private boolean publicLetter;
    /**
     * Date of Creation
     * Strictly UTC time
     */
    private LocalDateTime created;
    /**
     * The date after which the message becomes unavailable
     * Strictly UTC time
     */
    private LocalDateTime expirationDate;
    /**
     * Message id in messageStore
     */
    @JsonIgnore
    private String messageId;


    public Letter(String letterShortCode, String messageId, LocalDateTime expirationDate,
                  LocalDateTime created, boolean singleUse, boolean publicLetter) {
        this.letterShortCode = letterShortCode;
        this.messageId = messageId;
        this.expirationDate = expirationDate;
        this.singleUse = singleUse;
        this.publicLetter = publicLetter;
        this.created = created;
    }
}
