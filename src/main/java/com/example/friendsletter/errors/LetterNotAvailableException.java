package com.example.friendsletter.errors;

import lombok.Getter;

@Getter
public class LetterNotAvailableException extends Throwable {
    public static final int NOT_FOUND = 0;
    public static final int NOT_PUBLIC = 1;
    public static final int EXPIRED = 2;
    public static final int HAS_BEEN_READ = 3;

    public static final int MESSAGE_NOT_FOUND = 4;


    private final String letterShortCode;
    private final int errorCode;


    public LetterNotAvailableException(String letterShortCode, int errorCode) {
        this.letterShortCode = letterShortCode;
        this.errorCode = errorCode;
    }

}
