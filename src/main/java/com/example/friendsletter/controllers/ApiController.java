package com.example.friendsletter.controllers;


import com.example.friendsletter.data.Letter;
import com.example.friendsletter.data.LetterRequestDto;
import com.example.friendsletter.data.LetterResponseDto;
import com.example.friendsletter.data.LetterWithCountVisits;
import com.example.friendsletter.errors.LetterNotAvailableException;
import com.example.friendsletter.services.LetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api1")
@Tag(name = "Friend's letter", description = "publish and read letters")
public class ApiController {
    final private LetterService letterService;

    public ApiController(LetterService letterService) {
        this.letterService = letterService;
    }

    @Operation(summary = "Get list of latest letters")
    @GetMapping("/letters")
    Slice<Letter> getPublicLetters(@ParameterObject @PageableDefault(sort = "created",
            direction = Sort.Direction.DESC) Pageable pageable) {
        return letterService.getPublicLetters(pageable);
    }

    @Operation(summary = "Get list of the most popular public letters")
    @GetMapping("/letters/most-popular")
    List<LetterWithCountVisits> getMostPopular(@ParameterObject Pageable pageable) {
        return letterService.getMostPopular(pageable);
    }

    @Operation(summary = "Read the letter")
    @GetMapping("/letter/{shortLetterCode}")
    LetterResponseDto getLetterInfo(@PathVariable String shortLetterCode,
                                    HttpServletRequest request)
            throws LetterNotAvailableException {
        LetterResponseDto letterDto = letterService.readLetter(shortLetterCode);
        letterService.writeVisit(letterDto.getLetterShortCode(), request.getRemoteAddr());
        return letterDto;
    }

    @Operation(summary = "Save the letter and get its id",
            description = "Timezone in format Asia/Jakarta. Default timezone is UTC. " +
                    "Expiration date must be in the future")
    @PostMapping("/letter")
    LetterResponseDto saveLetter(@Valid @RequestBody LetterRequestDto letterDto) {
        return letterService.saveLetter(letterDto);
    }
}
