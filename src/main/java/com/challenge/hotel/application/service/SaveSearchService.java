package com.challenge.hotel.application.service;

import com.challenge.hotel.application.port.in.SaveSearchUseCase;
import com.challenge.hotel.application.port.out.SearchRepository;
import com.challenge.hotel.domain.model.HotelSearch;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SaveSearchService implements SaveSearchUseCase {

    private final SearchRepository repository;

    public SaveSearchService(SearchRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void save(HotelSearch search) {
        Objects.requireNonNull(search, "search must not be null");

        repository.save(search);
    }
}