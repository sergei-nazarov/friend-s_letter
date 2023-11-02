package com.example.friendsletter.repository;

import com.example.friendsletter.data.Letter;
import com.example.friendsletter.data.LetterWithCountVisits;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    Optional<Letter> findByLetterShortCode(String letterShortCode);

    Slice<Letter> findAllByPublicLetterIs(boolean publicLetter, Pageable page);

    List<Letter> findByLetterShortCodeIn(Collection<String> letterShortCode);

    @Query("""
            SELECT new com.example.friendsletter.data.LetterWithCountVisits(l, count(v)) 
            FROM Letter l
            LEFT JOIN l.visits v
            WHERE l.publicLetter=true 
            GROUP BY l
            ORDER BY count(v) DESC""")
//    @Query(value = "select l.visits from Letter l JOIN l.visits"
//            ,
//            countQuery = "select count(l) from Letter l"
    List<LetterWithCountVisits> getPopular(Pageable pageable);


}
