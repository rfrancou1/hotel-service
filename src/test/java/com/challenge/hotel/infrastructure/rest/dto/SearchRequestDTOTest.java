package com.challenge.hotel.infrastructure.rest.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;

class SearchRequestDTOTest {

    @Test
    void shouldAllowNullAgesForBeanValidation() {
        SearchRequestDTO request = new SearchRequestDTO(
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                null
        );

        assertNull(request.ages());
    }
}