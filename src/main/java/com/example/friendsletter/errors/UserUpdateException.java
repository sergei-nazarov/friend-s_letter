package com.example.friendsletter.errors;

import java.util.List;

public class UserUpdateException extends Exception {

    List<UserErrorHolder> userErrors;

    public UserUpdateException(List<UserErrorHolder> userErrors) {
        this.userErrors = userErrors;
    }

    public List<UserErrorHolder> getUserErrors() {
        return userErrors;
    }
}
