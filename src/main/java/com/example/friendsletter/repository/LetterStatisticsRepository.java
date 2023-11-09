package com.example.friendsletter.repository;

import com.example.friendsletter.data.LetterStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository with statistics of visits to letters
 */
public interface LetterStatisticsRepository extends JpaRepository<LetterStat, Long> {

    int countAllByLetterShortCodeIs(String messageShortCode);

    Optional<LetterStat> findFirstByLetterShortCodeIsAndIpIsAndVisitTimestampIsAfter(String letterShortCode, String ip, LocalDateTime visitTimestamp);


}
