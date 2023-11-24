package com.example.friendsletter.services;

import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserRegistrationDto;

import java.util.List;

public interface IUserService {
    void saveUser(UserRegistrationDto userDto);

    User findUserByEmail(String email);

    List<UserRegistrationDto> findAllUsers();

}
