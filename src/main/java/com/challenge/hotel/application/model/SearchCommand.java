package com.challenge.hotel.application.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record SearchCommand(
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {

    public SearchCommand {
        Objects.requireNonNull(hotelId, "hotelId must not be null");
        Objects.requireNonNull(checkIn, "checkIn must not be null");
        Objects.requireNonNull(checkOut, "checkOut must not be null");
        Objects.requireNonNull(ages, "ages must not be null");

        ages = List.copyOf(ages);
    }
}