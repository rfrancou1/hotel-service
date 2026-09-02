package com.challenge.hotel.infrastructure.kafka.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SearchEvent(
        UUID searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {

    public SearchEvent {
        ages = List.copyOf(ages);
    }
}