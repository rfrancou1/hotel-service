package com.challenge.hotel.infrastructure.rest.validation;

import com.challenge.hotel.infrastructure.rest.dto.SearchRequestDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidDateRangeValidatorTest {

    private final ValidDateRangeValidator validator =
            new ValidDateRangeValidator();

    @Test
    void shouldReturnTrueForValidDateRange() {
        SearchRequestDTO request = new SearchRequestDTO(
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void shouldReturnFalseWhenCheckInIsAfterCheckOut() {
        SearchRequestDTO request = new SearchRequestDTO(
                "1234aBc",
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 12, 29),
                List.of(30, 29, 1, 3)
        );

        assertFalse(validator.isValid(request, null));
    }

    @Test
    void shouldReturnFalseWhenDatesAreEqual() {
        LocalDate date = LocalDate.of(2026, 12, 29);

        SearchRequestDTO request = new SearchRequestDTO(
                "1234aBc",
                date,
                date,
                List.of(30, 29, 1, 3)
        );

        assertFalse(validator.isValid(request, null));
    }

    @Test
    void shouldReturnTrueWhenRequestIsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void shouldReturnTrueWhenCheckInIsNull() {
        SearchRequestDTO request = new SearchRequestDTO(
                "1234aBc",
                null,
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void shouldReturnTrueWhenCheckOutIsNull() {
        SearchRequestDTO request = new SearchRequestDTO(
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                null,
                List.of(30, 29, 1, 3)
        );

        assertTrue(validator.isValid(request, null));
    }
}