package com.example.friendsletter.services;

import com.example.friendsletter.data.Letter;
import com.example.friendsletter.data.LetterDto;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.repository.LetterRepository;
import com.example.friendsletter.services.messages.MessageStorage;
import com.example.friendsletter.services.url.UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class LetterService {

    private final MessageStorage messageStorage;
    private final UrlGenerator urlGenerator;
    private final LetterRepository repository;

    @Autowired
    public LetterService(MessageStorage messageStorage, UrlGenerator urlGenerator, LetterRepository repository) {
        this.messageStorage = messageStorage;
        this.urlGenerator = urlGenerator;
        this.repository = repository;
    }

    public LetterDto saveLetter(LetterDto letterDto) {
        String messageId = messageStorage.save(letterDto.getMessage());
        String letterShortCode = urlGenerator.generate();
        LocalDateTime utcExpDate = toUtc(letterDto.getExpirationDate(), letterDto.getTimezone());
        Letter letter = new Letter(letterShortCode, messageId,
                utcExpDate,
                letterDto.isSingleUse(),
                letterDto.isPublicLetter());
        repository.save(letter);
        letter.setExpirationDate(utcExpDate);
        letter.setLetterShortCode(letterShortCode);
        return new LetterDto(letterDto.getMessage(),
                utcExpDate, letter.isSingleUse(), letter.isPublicLetter(),
                null, letter.getCreated(), letter.getLetterShortCode());
    }

    public LetterDto readLetter(String letterShortCode) throws LetterNotAvailableException {
        Optional<Letter> letterOptional = repository.findByLetterShortCode(letterShortCode);
        if (letterOptional.isEmpty()) {
            throw new LetterNotAvailableException(letterShortCode, LetterNotAvailableException.NOT_FOUND);
        }
        Letter letter = letterOptional.get();
        String message = messageStorage.read(letter.getMessageId());
        if (LocalDateTime.now(ZoneOffset.UTC).isAfter(letter.getExpirationDate())) {
            throw new LetterNotAvailableException(letterShortCode, LetterNotAvailableException.EXPIRED);
        }//todo error singleUse
        return new LetterDto(message, letter.getExpirationDate(), letter.isSingleUse(),
                letter.isPublicLetter(), null, letter.getCreated(), letterShortCode);
    }

    private LocalDateTime toUtc(LocalDateTime dateTime, String zoneId) {
        if (dateTime == null) {
            return LocalDateTime.of(2100, 1, 1, 0, 0);
        }
        return dateTime.atZone(ZoneId.of(zoneId))
                .withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}
