package com.example.friendsletter.data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object for creating a letter
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterRequestDto {

    @Size(message = "Letter too big. Max 2147483647 symbols")
    @NotBlank(message = "Message is mandatory")
    private String message;
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expirationDate;
    private String timeZone;
    private boolean singleUse;
    private boolean publicLetter;
}