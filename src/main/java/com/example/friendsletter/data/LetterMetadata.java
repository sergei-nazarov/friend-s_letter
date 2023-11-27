package com.example.friendsletter.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entity for storage in database
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "letters_metadata")
public class LetterMetadata implements Serializable {

    @Id
    @Column(nullable = false)
    private String letterShortCode;

    @OneToMany(mappedBy = "letter", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    List<LetterStat> visits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_letters",
            joinColumns = {@JoinColumn(name = "LETTER_SHORT_CODE", referencedColumnName = "letterShortCode")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    @JsonIgnore
    @ToString.Exclude
    User user;

    /**
     * If true, the message can only be read 1 time
     */
    private boolean singleRead;
    /**
     * Can be shown in API and popular messages
     */
    private boolean publicLetter;

    /**
     * Title of the letter
     */
    private String title;
    /**
     * Author of the letter
     */
    private String author;
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
    private String messageId;

    public LetterMetadata(String letterShortCode, boolean singleRead, boolean publicLetter,
                          String title, String author, LocalDateTime created,
                          LocalDateTime expirationDate, String messageId) {
        this.letterShortCode = letterShortCode;
        this.singleRead = singleRead;
        this.publicLetter = publicLetter;
        this.title = title;
        this.author = author;
        this.created = created;
        this.expirationDate = expirationDate;
        this.messageId = messageId;
    }

    public LetterResponseDto toLetterResponseDto(String message) {
        return new LetterResponseDto(message, letterShortCode, created, expirationDate, title, author, singleRead, publicLetter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LetterMetadata that = (LetterMetadata) o;

        if (singleRead != that.singleRead) return false;
        if (publicLetter != that.publicLetter) return false;
        if (!letterShortCode.equals(that.letterShortCode)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(author, that.author)) return false;
        if (!created.equals(that.created)) return false;
        if (!expirationDate.equals(that.expirationDate)) return false;
        return messageId.equals(that.messageId);
    }

    @Override
    public int hashCode() {
        int result = letterShortCode.hashCode();
        result = 31 * result + (singleRead ? 1 : 0);
        result = 31 * result + (publicLetter ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + created.hashCode();
        result = 31 * result + expirationDate.hashCode();
        result = 31 * result + messageId.hashCode();
        return result;
    }
}
