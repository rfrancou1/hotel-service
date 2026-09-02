package com.challenge.hotel.application.service;

import com.challenge.hotel.application.model.SearchCount;
import com.challenge.hotel.application.port.in.CountSearchUseCase;
import com.challenge.hotel.application.port.out.SearchRepository;
import com.challenge.hotel.domain.exception.SearchNotFoundException;
import com.challenge.hotel.domain.model.HotelSearch;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CountSearchService implements CountSearchUseCase {

    private final SearchRepository repository;

    public CountSearchService(SearchRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public SearchCount getCount(UUID searchId) {
        Objects.requireNonNull(searchId, "searchId must not be null");

        HotelSearch search = repository.findById(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));

        long count = repository.countMatching(search);

        return new SearchCount(search, count);
    }
}