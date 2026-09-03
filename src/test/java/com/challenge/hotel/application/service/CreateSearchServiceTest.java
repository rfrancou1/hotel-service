package com.challenge.hotel.application.service;

import com.challenge.hotel.application.model.SearchCommand;
import com.challenge.hotel.application.port.out.SearchEventPublisher;
import com.challenge.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSearchServiceTest {

    @Mock
    private SearchEventPublisher searchEventPublisher;

    @InjectMocks
    private CreateSearchService createSearchService;

    @Test
    void shouldCreateSearchAndPublishEvent() {
        SearchCommand command = new SearchCommand(
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        UUID result = createSearchService.create(command);

        ArgumentCaptor<HotelSearch> captor =
                ArgumentCaptor.forClass(HotelSearch.class);

        verify(searchEventPublisher).publish(captor.capture());

        HotelSearch publishedSearch = captor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, publishedSearch.searchId()),
                () -> assertEquals(command.hotelId(), publishedSearch.hotelId()),
                () -> assertEquals(command.checkIn(), publishedSearch.checkIn()),
                () -> assertEquals(command.checkOut(), publishedSearch.checkOut()),
                () -> assertEquals(command.ages(), publishedSearch.ages())
        );

        verifyNoMoreInteractions(searchEventPublisher);
    }

    @Test
    void shouldPropagateExceptionWhenPublisherFails() {
        SearchCommand command = new SearchCommand(
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(searchEventPublisher)
                .publish(any(HotelSearch.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createSearchService.create(command)
        );

        assertEquals("Kafka unavailable", exception.getMessage());

        verify(searchEventPublisher)
                .publish(any(HotelSearch.class));
    }

    @Test
    void shouldRejectNullCommand() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> createSearchService.create(null)
        );

        assertEquals("command must not be null", exception.getMessage());

        verifyNoInteractions(searchEventPublisher);
    }
}