package com.example.friendsletter.repository;

import com.example.friendsletter.data.LetterStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterStatisticsRepository extends JpaRepository<LetterStat, Long> {

    int countAllByLetterShortCodeIs(String messageShortCode);


}
