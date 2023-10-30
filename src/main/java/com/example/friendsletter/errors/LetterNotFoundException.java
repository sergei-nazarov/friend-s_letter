package com.example.friendsletter.errors;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LetterNotFoundException extends Throwable {
    private final String letterShortCode;

    public LetterNotFoundException(String letterShortCode) {

        this.letterShortCode = letterShortCode;
    }
}
