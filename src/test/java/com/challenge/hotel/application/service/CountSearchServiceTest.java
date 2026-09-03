package com.challenge.hotel.application.service;

import com.challenge.hotel.application.model.SearchCount;
import com.challenge.hotel.application.port.out.SearchRepository;
import com.challenge.hotel.domain.exception.SearchNotFoundException;
import com.challenge.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountSearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private CountSearchService countSearchService;

    @Test
    void shouldReturnSearchCount() {
        UUID searchId = UUID.randomUUID();

        HotelSearch search = new HotelSearch(
                searchId,
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(searchRepository.findById(searchId))
                .thenReturn(Optional.of(search));

        when(searchRepository.countMatching(search))
                .thenReturn(3L);

        SearchCount result = countSearchService.getCount(searchId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(search, result.search()),
                () -> assertEquals(3L, result.count())
        );

        verify(searchRepository).findById(searchId);
        verify(searchRepository).countMatching(search);
        verifyNoMoreInteractions(searchRepository);
    }

    @Test
    void shouldThrowSearchNotFoundExceptionWhenSearchDoesNotExist() {
        UUID searchId = UUID.randomUUID();

        when(searchRepository.findById(searchId))
                .thenReturn(Optional.empty());

        SearchNotFoundException exception = assertThrows(
                SearchNotFoundException.class,
                () -> countSearchService.getCount(searchId)
        );

        assertEquals(
                "Search not found: %s".formatted(searchId),
                exception.getMessage()
        );

        verify(searchRepository).findById(searchId);
        verify(searchRepository, never()).countMatching(any());
        verifyNoMoreInteractions(searchRepository);
    }

    @Test
    void shouldRejectNullSearchId() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> countSearchService.getCount(null)
        );

        assertEquals("searchId must not be null", exception.getMessage());

        verifyNoInteractions(searchRepository);
    }
}