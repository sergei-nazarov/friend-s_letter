package com.example.friendsletter.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "letters")
public class Letter {

    @OneToMany(mappedBy = "letter", fetch = FetchType.LAZY)
    @JsonIgnore
    List<LetterStat> visits;
    private String letterShortCode;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "letter_gen")
    @SequenceGenerator(name = "letter_gen", sequenceName = "letters_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Long id;
    private LocalDateTime expirationDate;
    private boolean singleUse;
    private boolean publicLetter;
    private LocalDateTime created;
    @JsonIgnore
    private String messageId;

    //Lombok can't exclude field from @AllArgsConstructor, so...
    public Letter(String letterShortCode, String messageId, LocalDateTime expirationDate, boolean singleUse, boolean publicLetter) {
        this.letterShortCode = letterShortCode;
        this.messageId = messageId;
        this.expirationDate = expirationDate;
        this.singleUse = singleUse;
        this.publicLetter = publicLetter;
        this.created = LocalDateTime.now(ZoneOffset.UTC);
    }
}
