package com.example.friendsletter.services;

import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserRegistrationDto;

public interface IUserService {
    User saveUser(UserRegistrationDto userDto);

    User loadUserByUsernameOrEmail(String username, String email);

}
