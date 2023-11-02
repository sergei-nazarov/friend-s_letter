package com.example.friendsletter.controllers;


import com.example.friendsletter.data.Letter;
import com.example.friendsletter.data.LetterDto;
import com.example.friendsletter.data.LetterWithCountVisits;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.services.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.beans.Transient;
import java.util.List;

@RestController
@RequestMapping("api1")
public class ApiController {
    final private LetterService letterService;

    public ApiController(LetterService letterService) {
        this.letterService = letterService;
    }


    @GetMapping("/letters")
    Slice<Letter> getPublicLetters(
            @RequestParam(value = "offset", defaultValue = "0", required = false) @Min(0) int offset,
            @RequestParam(value = "limit", defaultValue = "10", required = false) @Max(100) int limit) {
        return letterService.getPublicLetters(PageRequest.of(offset, limit));
    }

    @GetMapping("/letters/most-popular")
    @Transient
    List<LetterWithCountVisits> getMostPopular(
            @RequestParam(value = "offset", defaultValue = "0", required = false) @Min(0) int offset,
            @RequestParam(value = "limit", defaultValue = "10", required = false) @Max(100) int limit
    ) {
        return letterService.getMostPopular(PageRequest.of(offset, limit));
    }

    @GetMapping("/letter/{shortLetterCode}")
    LetterDto getLetterInfo(@PathVariable String shortLetterCode,
                            HttpServletRequest request)
            throws LetterNotAvailableException {
        LetterDto letterDto = letterService.readLetter(shortLetterCode);
        //todo fix timezones to utc
        letterService.writeVisit(letterDto.getLetterShortCode(), request.getRemoteAddr());
        return letterDto;
    }

}
