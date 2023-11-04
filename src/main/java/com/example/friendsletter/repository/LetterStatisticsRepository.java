package com.example.friendsletter.repository;

import com.example.friendsletter.data.LetterStat;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository with statistics of visits to letters
 */
public interface LetterStatisticsRepository extends JpaRepository<LetterStat, Long> {

    int countAllByLetterShortCodeIs(String messageShortCode);


}
