package com.example.friendsletter.repository;

import com.example.friendsletter.data.LetterMetadata;
import com.example.friendsletter.data.PopularLetterResponseDto;
import com.example.friendsletter.data.User;
import lombok.NonNull;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository with Letter metadata
 */
public interface LetterMetadataRepository extends JpaRepository<LetterMetadata, String> {

    @Cacheable(cacheNames = "letter")
    Optional<LetterMetadata> findByLetterShortCode(String letterShortCode);

    Slice<LetterMetadata> findAllByPublicLetterIsAndSingleReadIs(boolean publicLetter, boolean singleRead, Pageable page);

    @Query("""
            SELECT new com.example.friendsletter.data.PopularLetterResponseDto(l, count(v))
            FROM LetterMetadata l
            LEFT JOIN l.visits v
            WHERE l.publicLetter=true
            GROUP BY l
            ORDER BY count(v) DESC""")
    List<PopularLetterResponseDto> getPopular(Pageable pageable);

    List<LetterMetadata> findByUser(User user, Pageable pageable);

    @CachePut(cacheNames = "letter", key = "#entity.letterShortCode")
    @Override
    <S extends LetterMetadata> @NonNull S save(@NonNull S entity);


}
