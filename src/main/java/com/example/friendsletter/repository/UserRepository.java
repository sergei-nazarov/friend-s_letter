package com.example.friendsletter.repository;

import com.example.friendsletter.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByUsername(String username);

    User findFirstByUsernameOrEmail(String username, String email);

}