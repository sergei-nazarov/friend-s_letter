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
    @NotBlank(message = "{letter.validation.text_is_mandatory}")
    private String message;
    @Future(message = "{letter.validation.expiry_date_in_past}")
    private LocalDateTime expirationDate;
    private String timeZone;
    @Size(max = 100, message = "{letter.validation.title_too_long}")
    private String title;
    @Size(max = 100, message = "{letter.validation.author_too_long}")
    private String author;
    private boolean singleRead;
    private boolean publicLetter;
}
