package com.example.friendsletter.repository;

import com.example.friendsletter.data.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LettersRepository extends JpaRepository<Letter, Long> {
}
