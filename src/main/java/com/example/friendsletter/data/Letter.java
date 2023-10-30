package com.example.friendsletter.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "letter_gen")
    @SequenceGenerator(name = "letter_gen", sequenceName = "letters_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String messageShortCode;
    private String messageId;
    private LocalDateTime expirationDate;
    private boolean singleUse;
    private boolean publicLetter;

    //Lombok can't exclude field from @AllArgsConstructor, so...
    public Letter(String messageShortCode, String messageId, LocalDateTime expirationDate, boolean singleUse, boolean publicLetter) {
        this.messageShortCode = messageShortCode;
        this.messageId = messageId;
        this.expirationDate = expirationDate;
        this.singleUse = singleUse;
        this.publicLetter = publicLetter;
    }
}
