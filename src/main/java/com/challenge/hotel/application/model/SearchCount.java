package com.challenge.hotel.application.model;

import com.challenge.hotel.domain.model.HotelSearch;

import java.util.Objects;

public record SearchCount(
        HotelSearch search,
        long count
) {

    public SearchCount {
        Objects.requireNonNull(search, "search must not be null");

        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
    }
}