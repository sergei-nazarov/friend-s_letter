package com.example.friendsletter.services;

import com.example.friendsletter.data.*;
import com.example.friendsletter.errors.LETTER_ERROR_STATUS;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.repository.LetterMetadataRepository;
import com.example.friendsletter.repository.LetterStatisticsRepository;
import com.example.friendsletter.services.Cache.MessageCache;
import com.example.friendsletter.services.messages.MessageStorage;
import com.example.friendsletter.services.url.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main letter service
 */
@Component
@Slf4j
public class LetterService {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private final MessageStorage messageStorage;
    private final MessageCache messageCache;
    private final SequenceGenerator urlGenerator;
    private final LetterMetadataRepository letterRepository;
    private final LetterStatisticsRepository letterStatRepository;

    private final ExecutorService executor = Executors.newWorkStealingPool(6);
    private volatile List<PopularLetterResponseDto> mostPopularMessages = new ArrayList<>();


    @Autowired
    public LetterService(MessageStorage messageStorage, MessageCache messageCache,
                         SequenceGenerator urlGenerator, LetterMetadataRepository repository,
                         LetterStatisticsRepository letterStatRepository) {
        this.messageStorage = messageStorage;
        this.messageCache = messageCache;
        this.urlGenerator = urlGenerator;
        this.letterRepository = repository;
        this.letterStatRepository = letterStatRepository;
    }

    /**
     * @param letterDto - message info to save
     * @return response with letterShortCode
     * Letter metadata will be stored in DB, message in messageStore and cache
     */
    public LetterResponseDto saveLetter(LetterRequestDto letterDto) {
        String message = letterDto.getMessage();
        String letterShortCode = urlGenerator.generate();

        //put message in storage and cache
        String messageId = messageStorage.save(message);
        messageCache.save(new MessageCacheDto(messageId, message));

        ZoneId tz;
        try {
            tz = ZoneId.of(letterDto.getTimeZone());
        } catch (Exception ignore) {
            tz = UTC;
        }

        LocalDateTime utcExpDate = toUtc(letterDto.getExpirationDate(), tz);
        LocalDateTime utcCreated = LocalDateTime.now(ZoneOffset.UTC);

        LetterMetadata letter = new LetterMetadata(letterShortCode, letterDto.isSingleRead(), letterDto.isPublicLetter(),
                letterDto.getTitle(), letterDto.getAuthor(),
                utcCreated, utcExpDate, messageId);
        letterRepository.save(letter);
        return letter.toLetterResponseDto(message);
    }

    /**
     * @param letterShortCode - letterShortCode
     * @return Data for displaying letter
     * Read the message in the letter. The message will be searched in the cache first.
     * If it is not found, it will be requested in messageStore
     * @throws LetterNotAvailableException - different errors with letter.
     */
    public LetterResponseDto readLetter(String letterShortCode) throws LetterNotAvailableException {

        Optional<LetterMetadata> letterOptional = letterRepository.findByLetterShortCode(letterShortCode);
        if (letterOptional.isEmpty()) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.LETTER_NOT_FOUND);
        }
        LetterMetadata letter = letterOptional.get();
        if (LocalDateTime.now(UTC).isAfter(letter.getExpirationDate())) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.EXPIRED);
        } else if (letter.isSingleRead() && letterStatRepository.countAllByLetterShortCodeIs(letterShortCode) > 0) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.HAS_BEEN_READ);
        }
        String messageId = letter.getMessageId();

        String message;
        try {
            message = getMessageText(messageId);
        } catch (FileNotFoundException e) {
            throw new LetterNotAvailableException(letterShortCode, LETTER_ERROR_STATUS.MESSAGE_NOT_FOUND);
        }

        return letter.toLetterResponseDto(message);
    }

    /**
     * @param messageId - message id
     *                  Looking for message in cache, then in message store
     * @return message text
     * @throws FileNotFoundException if message not found
     */
    public String getMessageText(String messageId) throws FileNotFoundException {
        Optional<MessageCacheDto> cachedMessage = messageCache.get(messageId);
        String message;
        if (cachedMessage.isPresent()) {
            message = cachedMessage.get().getMessage();
            log.info("Message with id " + messageId + " has been found in cache");
        } else {
            message = messageStorage.read(messageId);
            messageCache.save(new MessageCacheDto(messageId, message));
        }
        return message;
    }

    /**
     * Write letter visit to DB
     * Write only if there are no visits for last 5 minutes
     * with same ip and letter code
     */
    public void writeVisit(String letterShortCode, String ip) {
        executor.execute(() -> {
            Optional<LetterStat> letterStat = letterStatRepository
                    .findFirstByLetterShortCodeIsAndIpIsAndVisitTimestampIsAfter(
                            letterShortCode,
                            ip,
                            LocalDateTime.now(ZoneOffset.UTC).minusMinutes(5));
            if (letterStat.isEmpty()) {
                letterStatRepository.save(new LetterStat(LocalDateTime.now(UTC), ip, letterShortCode));
            }
        });
    }

    public void writeVisitUnfair(String letterShortCode) {
        letterStatRepository.save(new LetterStat(LocalDateTime.now(UTC), "unknown", letterShortCode));
    }

    public LocalDateTime toUtc(LocalDateTime dateTime, ZoneId timeZone) {
        if (dateTime == null) {
            return LocalDateTime.of(2100, 1, 1, 0, 0, 0);
        }
        return dateTime.atZone(timeZone)
                .withZoneSameInstant(UTC).toLocalDateTime();
    }

    /**
     * @return List of public letters
     */
    public Slice<LetterResponseDto> getPublicLetters(Pageable pageable) {
        Slice<LetterMetadata> letterMetadata = letterRepository
                .findAllByPublicLetterIsAndSingleReadIs(true, false, pageable);
        List<LetterResponseDto> letterResponseDtos = letterMetadata.getContent().parallelStream().map(letter -> {
            String message;
            try {
                message = getMessageText(letter.getMessageId());
            } catch (FileNotFoundException e) {
                message = null;
            }
            return letter.toLetterResponseDto(message);
        }).toList();
        return new SliceImpl<>(letterResponseDtos, letterMetadata.getPageable(), letterMetadata.hasNext());
    }

    /**
     * @return - List of the most popular letters with count of visits
     */
    public List<PopularLetterResponseDto> getMostPopular() {
        return new ArrayList<>(mostPopularMessages);
    }


    /**
     * Finding the most popular messages every 5 minutes
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    private void findingPopularMessages() {
        log.debug("Start looking for popular messages...");
        List<PopularLetterResponseDto> letters = letterRepository
                .getPopular(PageRequest.of(0, 10));
        mostPopularMessages = letters.stream().peek(letterDto -> {
            try {
                letterDto.setMessage(getMessageText(letterDto.getMessageId()));
            } catch (FileNotFoundException ignore) {
            }
        }).filter(letterDto -> letterDto.getMessage() != null).toList();
    }
}
