package com.example.friendsletter.repository;

import com.example.friendsletter.data.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    Optional<Letter> findByLetterShortCode(String letterShortCode);

}
