package com.example.friendsletter.errors;

import lombok.Getter;

@Getter
public class LetterNotAvailableException extends Throwable {

    private final String letterShortCode;
    private final LETTER_ERROR_STATUS status;


    public LetterNotAvailableException(String letterShortCode, LETTER_ERROR_STATUS status) {
        this.letterShortCode = letterShortCode;
        this.status = status;
    }

}
