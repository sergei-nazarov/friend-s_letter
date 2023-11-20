package com.example.friendsletter;


import com.example.friendsletter.data.LetterMetadata;
import com.example.friendsletter.data.LetterStat;
import com.example.friendsletter.repository.LetterMetadataRepository;
import com.example.friendsletter.repository.LetterStatisticsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
@ActiveProfiles("test")
public class JpaTests {

    @Autowired
    LetterMetadataRepository letterMetadataRepository;
    @Autowired
    LetterStatisticsRepository letterStatisticsRepository;


    @Test
    void saveLetterMetaDataTest() {
        LetterMetadata origin = new LetterMetadata("barbi",
                true, true, "mega-letter",
                "Stiven", LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), "1234567890qwertyui");
        letterMetadataRepository.save(origin);
        Optional<LetterMetadata> other = letterMetadataRepository
                .findByLetterShortCode(origin.getLetterShortCode());
        assertEquals(origin, other.get());
    }

    @Test
    void saveLetterStatTest() {
        String letterCode = "qwerty";
        LocalDateTime now = LocalDateTime.now();
        letterMetadataRepository.save(new LetterMetadata(letterCode, true, true, "", "", now, now, ""));
        LetterStat origin = new LetterStat(now, "12.212.12", letterCode);
        letterStatisticsRepository.save(origin);
        LetterStat other = letterStatisticsRepository.findAll().get(0);
        assertEquals(origin, other);
    }

}
