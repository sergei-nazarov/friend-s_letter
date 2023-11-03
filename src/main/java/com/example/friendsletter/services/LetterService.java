package com.example.friendsletter.services;

import com.example.friendsletter.data.*;
import com.example.friendsletter.errors.LETTER_ERROR_STATUS;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.repository.LetterRepository;
import com.example.friendsletter.repository.LetterStatisticsRepository;
import com.example.friendsletter.services.Cache.MessageCache;
import com.example.friendsletter.services.messages.MessageStorage;
import com.example.friendsletter.services.url.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LetterService {
    private static final ZoneId UTC = ZoneId.of("UTC");
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

    public LetterResponseDto saveLetter(LetterRequestDto letterDto) {
        String message = letterDto.getMessage();
        String letterShortCode = urlGenerator.generate();

        //put message in storage and cache
        String messageId = messageStorage.save(message);
        messageCache.set(new MessageCacheDto(messageId, message));

        ZoneId tz;
        try {
            tz = ZoneId.of(letterDto.getTimeZone());
        } catch (Exception ignore) {
            tz = UTC;
        }

        LocalDateTime utcExpDate = toUtc(letterDto.getExpirationDate(), tz);
        LocalDateTime created = LocalDateTime.now(tz);
        Letter letter = new Letter(letterShortCode, messageId,
                utcExpDate, toUtc(created, tz), letterDto.isSingleUse(), letterDto.isPublicLetter());
        letterRepository.save(letter);

        return new LetterResponseDto(letterDto.getMessage(),
                letterShortCode, created, fromUtc(utcExpDate, tz),
                tz.getId(), letter.isSingleUse(), letter.isPublicLetter());
    }

    public LetterResponseDto readLetter(String letterShortCode) throws LetterNotAvailableException {

        Optional<Letter> letterOptional = letterRepository.findByLetterShortCode(letterShortCode);
        if (letterOptional.isEmpty()) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.NOT_FOUND);
        }
        Letter letter = letterOptional.get();
        if (LocalDateTime.now(UTC).isAfter(letter.getExpirationDate())) {
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
        return new LetterResponseDto(message, letterShortCode, letter.getCreated(),
                letter.getExpirationDate(), UTC.getId(),
                letter.isSingleUse(), letter.isPublicLetter());
    }

    public void writeVisit(String letterShortCode, String ip) {
        executor.execute(() -> {
            LetterStat letterStat = new LetterStat(
                    LocalDateTime.now(UTC), ip, letterShortCode);
            letterStatRepository.save(letterStat);
        });
    }

    public LocalDateTime toUtc(LocalDateTime dateTime, ZoneId timeZone) {
        if (dateTime == null) {
            return LocalDateTime.of(2100, 1, 1, 0, 0, 0);
        }
        return dateTime.atZone(timeZone)
                .withZoneSameInstant(UTC).toLocalDateTime();
    }

    public LocalDateTime toNotNull(LocalDateTime dateTime) {
        if (dateTime == null) {
            return LocalDateTime.of(2100, 1, 1, 0, 0);
        }
        return dateTime;

    }

    public LocalDateTime fromUtc(LocalDateTime dateTime, ZoneId timeZone) {
        if (dateTime == null) {
            return ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, UTC)
                    .withZoneSameInstant(timeZone).toLocalDateTime();
        }
        return dateTime.atZone(UTC)
                .withZoneSameInstant(timeZone).toLocalDateTime();
    }

    public Slice<Letter> getPublicLetters(Pageable pageable) {
        return letterRepository.findAllByPublicLetterIs(true, pageable);
    }

    public List<LetterWithCountVisits> getMostPopular(Pageable pageable) {
        return letterRepository.getPopular(pageable);
    }
}
