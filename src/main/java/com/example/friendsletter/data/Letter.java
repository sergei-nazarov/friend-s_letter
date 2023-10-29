package com.example.friendsletter.data;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String messageShortCode;
    private String messageId;
    private LocalDateTime expirationDate;
    private boolean singleUse;
    private boolean publicLetter;

}
