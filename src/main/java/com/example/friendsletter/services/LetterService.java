package com.example.friendsletter.services;

import com.example.friendsletter.data.Letter;
import com.example.friendsletter.data.LetterDto;
import com.example.friendsletter.repository.LettersRepository;
import com.example.friendsletter.services.messages.MessageStorage;
import com.example.friendsletter.services.url.UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class LetterService {

    private final MessageStorage messageStorage;
    private final UrlGenerator urlGenerator;
    private final LettersRepository repository;

    @Autowired
    public LetterService(MessageStorage messageStorage, UrlGenerator urlGenerator, LettersRepository repository) {
        this.messageStorage = messageStorage;
        this.urlGenerator = urlGenerator;
        this.repository = repository;
    }

    public Letter saveLetter(LetterDto letterDto) {
        String messageId = messageStorage.save(letterDto.getMessage());
        String messageShortCode = urlGenerator.generate();
        LocalDateTime messageUtcDateTime = toUtc(letterDto.getExpirationDate(), letterDto.getTimezone());
        Letter letter = new Letter(messageShortCode, messageId,
                messageUtcDateTime,
                letterDto.isSingleUse(),
                letterDto.isPublicLetter());
        return repository.save(letter);
    }

    private LocalDateTime toUtc(LocalDateTime dateTime, String zoneId) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneId.of(zoneId))
                .withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

}
