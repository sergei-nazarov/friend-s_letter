package com.example.friendsletter.errors;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LetterNotAvailableException extends Throwable {
    private final String letterShortCode;
    private final String message;

    public LetterNotAvailableException(String letterShortCode, String message) {

        this.letterShortCode = letterShortCode;
        this.message = message;
    }
}
