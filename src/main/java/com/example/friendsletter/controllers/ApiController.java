package com.example.friendsletter.controllers;


import com.example.friendsletter.data.LetterRequestDto;
import com.example.friendsletter.data.LetterResponseDto;
import com.example.friendsletter.data.PopularLetterResponseDto;
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
import java.util.Map;

/**
 * API Controller
 */
@RestController
@RequestMapping("api1")
@Tag(name = "Friend's letter", description = "publish and read letters")
public class ApiController {
    final private LetterService letterService;

    public ApiController(LetterService letterService) {
        this.letterService = letterService;
    }

    @Operation(summary = "Get list of the latest letters")
    @GetMapping("/letters")
    Slice<LetterResponseDto> getPublicLetters(@ParameterObject @PageableDefault(sort = "created",
            direction = Sort.Direction.DESC) Pageable pageable) {
        return letterService.getPublicLetters(pageable);
    }

    @Operation(summary = "Get list of the most popular public letters")
    @GetMapping("/letters/most-popular")
    List<PopularLetterResponseDto> getMostPopular() {
        return letterService.getMostPopular();
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
            description = "Timezone in format like Asia/Jakarta. Default timezone is UTC. " +
                    "Expiration date must be in the future")
    @PostMapping("/letter")
    LetterResponseDto saveLetter(@Valid @RequestBody LetterRequestDto letterDto) {
        return letterService.saveLetter(letterDto);
    }

    /**
     * Unfair method :)
     */
    @Operation(hidden = true)
    @PostMapping("/boost")
    void boostViews(@RequestBody Map<String, String> map) {
        String letterShortCode = map.get("letterShortCode");
        int count = Integer.parseInt(map.get("count"));
        for (int i = 0; i < count; i++) {
            letterService.writeVisitUnfair(letterShortCode);
        }

    }
}
