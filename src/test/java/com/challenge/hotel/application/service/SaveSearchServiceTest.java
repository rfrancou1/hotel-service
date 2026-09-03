package com.challenge.hotel.application.service;

import com.challenge.hotel.application.port.out.SearchRepository;
import com.challenge.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveSearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SaveSearchService saveSearchService;

    @Test
    void shouldSaveSearch() {
        HotelSearch search = new HotelSearch(
                UUID.randomUUID(),
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        saveSearchService.save(search);

        verify(searchRepository).save(search);
        verifyNoMoreInteractions(searchRepository);
    }

    @Test
    void shouldRejectNullSearch() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> saveSearchService.save(null)
        );

        assertEquals("search must not be null", exception.getMessage());

        verifyNoInteractions(searchRepository);
    }

    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        HotelSearch search = new HotelSearch(
                UUID.randomUUID(),
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        doThrow(new RuntimeException("Database unavailable"))
                .when(searchRepository)
                .save(search);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> saveSearchService.save(search)
        );

        assertEquals("Database unavailable", exception.getMessage());

        verify(searchRepository).save(search);
    }
}