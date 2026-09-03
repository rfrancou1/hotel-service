package com.challenge.hotel.infrastructure.kafka.consumer;

import com.challenge.hotel.application.port.in.SaveSearchUseCase;
import com.challenge.hotel.domain.model.HotelSearch;
import com.challenge.hotel.infrastructure.kafka.model.SearchEvent;
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
class KafkaSearchConsumerTest {

    @Mock
    private SaveSearchUseCase saveSearchUseCase;

    @InjectMocks
    private KafkaSearchConsumer kafkaSearchConsumer;

    @Test
    void shouldConsumeEventAndSaveSearch() {
        UUID searchId = UUID.randomUUID();

        SearchEvent event = new SearchEvent(
                searchId,
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        kafkaSearchConsumer.consume(event);

        ArgumentCaptor<HotelSearch> captor =
                ArgumentCaptor.forClass(HotelSearch.class);

        verify(saveSearchUseCase).save(captor.capture());

        HotelSearch savedSearch = captor.getValue();

        assertAll(
                () -> assertEquals(searchId, savedSearch.searchId()),
                () -> assertEquals("1234aBc", savedSearch.hotelId()),
                () -> assertEquals(
                        LocalDate.of(2026, 12, 29),
                        savedSearch.checkIn()
                ),
                () -> assertEquals(
                        LocalDate.of(2026, 12, 31),
                        savedSearch.checkOut()
                ),
                () -> assertEquals(
                        List.of(30, 29, 1, 3),
                        savedSearch.ages()
                )
        );

        verifyNoMoreInteractions(saveSearchUseCase);
    }

    @Test
    void shouldPropagateExceptionWhenSaveFails() {
        SearchEvent event = new SearchEvent(
                UUID.randomUUID(),
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        doThrow(new RuntimeException("Database unavailable"))
                .when(saveSearchUseCase)
                .save(any(HotelSearch.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> kafkaSearchConsumer.consume(event)
        );

        assertEquals("Database unavailable", exception.getMessage());

        verify(saveSearchUseCase).save(any(HotelSearch.class));
    }
}