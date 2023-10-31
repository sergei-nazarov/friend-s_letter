package com.example.friendsletter.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "letters_stat")
@Getter
@NoArgsConstructor
public class LetterStat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "letter_stat_gen")
    @SequenceGenerator(name = "letter_stat_gen", sequenceName = "LETTERS_STAT_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDateTime visitTimestamp;
    private String ip;
    private String letterShortCode;

    public LetterStat(LocalDateTime visitTimestamp, String ip, String letterShortCode) {
        this.visitTimestamp = visitTimestamp;
        this.ip = ip;
        this.letterShortCode = letterShortCode;
    }
}
