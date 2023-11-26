package com.example.friendsletter.services;

import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserDto;
import com.example.friendsletter.errors.UserUpdateException;

public interface IUserService {
    User saveUser(UserDto userDto);

    User updateUser(User user, UserDto updatedDto) throws UserUpdateException;


    User loadUserByUsernameOrEmail(String username, String email);

}
