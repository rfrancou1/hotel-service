package com.challenge.hotel.domain.repository;

import com.challenge.hotel.domain.model.HotelSearch;

import java.util.Optional;
import java.util.UUID;

public interface SearchRepository {

    void save(HotelSearch search);

    Optional<HotelSearch> findById(UUID searchId);

    long countMatching(HotelSearch search);
}