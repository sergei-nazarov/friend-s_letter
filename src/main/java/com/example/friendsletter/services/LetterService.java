package com.example.friendsletter.services;

import com.example.friendsletter.data.Letter;
import com.example.friendsletter.data.LetterDto;
import com.example.friendsletter.data.LetterStat;
import com.example.friendsletter.data.MessageCacheDto;
import com.example.friendsletter.errors.LETTER_ERROR_STATUS;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.repository.LetterRepository;
import com.example.friendsletter.repository.LetterStatisticsRepository;
import com.example.friendsletter.services.Cache.MessageCache;
import com.example.friendsletter.services.messages.MessageStorage;
import com.example.friendsletter.services.url.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LetterService {
    private final MessageStorage messageStorage;
    private final MessageCache messageCache;
    private final SequenceGenerator urlGenerator;
    private final LetterRepository letterRepository;
    private final LetterStatisticsRepository letterStatRepository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public LetterService(MessageStorage messageStorage, MessageCache messageCache,
                         SequenceGenerator urlGenerator, LetterRepository repository,
                         LetterStatisticsRepository letterStatRepository) {
        this.messageStorage = messageStorage;
        this.messageCache = messageCache;
        this.urlGenerator = urlGenerator;
        this.letterRepository = repository;
        this.letterStatRepository = letterStatRepository;
    }

    public LetterDto saveLetter(LetterDto letterDto) {
        String message = letterDto.getMessage();
        String letterShortCode = urlGenerator.generate();

        //put message in storage and cache
        String messageId = messageStorage.save(message);
        messageCache.set(new MessageCacheDto(messageId, message));

        LocalDateTime utcExpDate = toUtc(letterDto.getExpirationDate(), letterDto.getTimezone());
        Letter letter = new Letter(letterShortCode, messageId,
                utcExpDate, letterDto.isSingleUse(), letterDto.isPublicLetter());
        letterRepository.save(letter);

        return new LetterDto(letterDto.getMessage(),
                fromUtc(utcExpDate, letterDto.getTimezone()), letter.isSingleUse(),
                letter.isPublicLetter(), letterDto.getTimezone(),
                fromUtc(letter.getCreated(), letterDto.getTimezone()), letterShortCode);
    }

    public LetterDto readLetter(String letterShortCode) throws LetterNotAvailableException {

        Optional<Letter> letterOptional = letterRepository.findByLetterShortCode(letterShortCode);
        if (letterOptional.isEmpty()) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.NOT_FOUND);
        }
        Letter letter = letterOptional.get();
        if (LocalDateTime.now(ZoneOffset.UTC).isAfter(letter.getExpirationDate())) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.EXPIRED);
        } else if (letter.isSingleUse() && letterStatRepository.countAllByLetterShortCodeIs(letterShortCode) > 0) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.HAS_BEEN_READ);
        }
        String message;
        String messageId = letter.getMessageId();

        Optional<MessageCacheDto> cachedMessage = messageCache.get(messageId);
        try {
            if (cachedMessage.isPresent()) {
                message = cachedMessage.get().getMessage();
                log.info("Message with id " + messageId + " has been found in cache");
            } else {
                message = messageStorage.read(messageId);
                messageCache.set(new MessageCacheDto(messageId, message));
            }
        } catch (FileNotFoundException e) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.MESSAGE_NOT_FOUND);
        }
        return new LetterDto(message, letter.getExpirationDate(), letter.isSingleUse(),
                letter.isPublicLetter(), null, letter.getCreated(), letterShortCode);
    }

    public void writeVisit(String letterShortCode, String ip) {
        executor.execute(() -> {
            LetterStat letterStat = new LetterStat(
                    LocalDateTime.now(ZoneOffset.UTC), ip, letterShortCode);
            letterStatRepository.save(letterStat);
        });
    }

    public LocalDateTime toUtc(LocalDateTime dateTime, String zoneId) {
        if (dateTime == null) {
            return LocalDateTime.of(2100, 1, 1, 0, 0);
        }
        return dateTime.atZone(ZoneId.of(zoneId))
                .withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public LocalDateTime fromUtc(LocalDateTime dateTime, ZoneId zoneId) {
        if (dateTime == null) {
            return LocalDateTime.of(2100, 1, 1, 0, 0);
        }
        return dateTime.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneId).toLocalDateTime();
    }

    public LocalDateTime fromUtc(LocalDateTime dateTime, TimeZone timeZone) {
        return fromUtc(dateTime, timeZone.toZoneId());
    }

    public LocalDateTime fromUtc(LocalDateTime dateTime, String zoneId) {
        return fromUtc(dateTime, ZoneId.of(zoneId));
    }
}
