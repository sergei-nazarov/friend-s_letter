package com.example.friendsletter.data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LetterDto {

    @Size(message = "Letter too big. Max 2147483647 symbols")
    @NotBlank(message = "Message is mandatory")
    private String message;
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expirationDate;
    private boolean singleUse;
    private boolean publicLetter;
    private String timezone;
}
