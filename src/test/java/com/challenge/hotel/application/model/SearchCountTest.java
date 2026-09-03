package com.challenge.hotel.application.model;

import com.challenge.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SearchCountTest {

    @Test
    void shouldRejectNegativeCount() {
        HotelSearch search = new HotelSearch(
                UUID.randomUUID(),
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SearchCount(search, -1)
        );

        assertEquals(
                "count must not be negative",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullSearch() {
        assertThrows(
                NullPointerException.class,
                () -> new SearchCount(null, 1)
        );
    }
}