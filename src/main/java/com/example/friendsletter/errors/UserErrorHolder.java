package com.example.friendsletter.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserErrorHolder {
    private USER_ERRORS error;
    private String usernameOrEmail;
}
