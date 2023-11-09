package com.example.friendsletter.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LetterWithCountVisits {
    private LetterMetadata letter;
    private long countVisits;

}
